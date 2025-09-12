package com.yoesoff.plate.service;

import com.yoesoff.plate.dto.FriendDTO;
import com.yoesoff.plate.dto.FriendRequestDTO;
import com.yoesoff.plate.entity.FriendshipEntity;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.FriendshipStatus;
import com.yoesoff.plate.enums.OrganizationType;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FriendshipService {

    public List<FriendDTO> getFriends(UserEntity user, int page, int size) {
        List<FriendshipEntity> friendships = FriendshipEntity.find(
                        "(requester = ?1 or receiver = ?1) and status = 'ACCEPTED'",
                        Sort.by("acceptedAt").descending(),
                        user)
                .page(page, size)
                .list();

        return friendships.stream()
                .map(friendship -> convertToDTO(friendship, user))
                .toList();
    }

    public List<FriendDTO> getPendingRequests(UserEntity user, boolean sent, int page, int size) {
        String query = sent ?
                "requester = ?1 and status = 'PENDING'" :
                "receiver = ?1 and status = 'PENDING'";

        List<FriendshipEntity> requests = FriendshipEntity.find(query,
                        Sort.by("createdAt").descending(),
                        user)
                .page(page, size)
                .list();

        return requests.stream()
                .map(request -> convertToDTO(request, user))
                .toList();
    }

    @Transactional
    public FriendDTO sendFriendRequest(UserEntity requester, FriendRequestDTO request) {
        UserEntity receiver = UserEntity.findById(request.receiverId);
        if (receiver == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (requester.equals(receiver)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        // Check if friendship already exists
        FriendshipEntity existing = FriendshipEntity.findFriendship(requester, receiver);
        if (existing != null) {
            if (existing.status == FriendshipStatus.PENDING) {
                throw new IllegalArgumentException("Friend request already sent");
            } else if (existing.status == FriendshipStatus.ACCEPTED) {
                throw new IllegalArgumentException("Already friends");
            } else if (existing.status == FriendshipStatus.BLOCKED) {
                throw new IllegalArgumentException("Cannot send friend request");
            }
        }

        FriendshipEntity friendship = new FriendshipEntity();
        friendship.requester = requester;
        friendship.receiver = receiver;
        friendship.status = FriendshipStatus.PENDING;
        friendship.requestMessage = request.message;
        friendship.createdAt = LocalDateTime.now();

        friendship.persist();
        return convertToDTO(friendship, requester);
    }

    @Transactional
    public FriendDTO acceptFriendRequest(UUID friendshipId, UserEntity receiver) {
        FriendshipEntity friendship = FriendshipEntity.findById(friendshipId);
        if (friendship == null || !friendship.receiver.equals(receiver)) {
            return null;
        }

        if (friendship.status != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Can only accept pending requests");
        }

        friendship.status = FriendshipStatus.ACCEPTED;
        friendship.acceptedAt = LocalDateTime.now();
        friendship.persist();

        return convertToDTO(friendship, receiver);
    }

    @Transactional
    public FriendDTO rejectFriendRequest(UUID friendshipId, UserEntity receiver) {
        FriendshipEntity friendship = FriendshipEntity.findById(friendshipId);
        if (friendship == null || !friendship.receiver.equals(receiver)) {
            return null;
        }

        friendship.status = FriendshipStatus.REJECTED;
        friendship.rejectedAt = LocalDateTime.now();
        friendship.persist();

        return convertToDTO(friendship, receiver);
    }

    @Transactional
    public boolean removeFriend(UUID friendshipId, UserEntity user) {
        FriendshipEntity friendship = FriendshipEntity.findById(friendshipId);
        if (friendship == null) {
            return false;
        }

        // Either user can remove the friendship
        boolean canRemove = friendship.requester.equals(user) || friendship.receiver.equals(user);
        if (!canRemove) {
            return false;
        }

        friendship.delete();
        return true;
    }

    @Transactional
    public boolean blockUser(UserEntity blocker, UUID userId) {
        UserEntity blocked = UserEntity.findById(userId);
        if (blocked == null || blocker.equals(blocked)) {
            return false;
        }

        FriendshipEntity existing = FriendshipEntity.findFriendship(blocker, blocked);
        if (existing != null) {
            existing.status = FriendshipStatus.BLOCKED;
            existing.blockedAt = LocalDateTime.now();
            existing.persist();
        } else {
            // Create new blocked relationship
            FriendshipEntity friendship = new FriendshipEntity();
            friendship.requester = blocker;
            friendship.receiver = blocked;
            friendship.status = FriendshipStatus.BLOCKED;
            friendship.blockedAt = LocalDateTime.now();
            friendship.createdAt = LocalDateTime.now();
            friendship.persist();
        }

        return true;
    }

    public boolean areFriends(UserEntity user1, UserEntity user2) {
        return FriendshipEntity.areFriends(user1, user2);
    }

    public long getFriendCount(UserEntity user) {
        return FriendshipEntity.countFriends(user);
    }

    public List<UserEntity> suggestFriends(UserEntity user, int limit) {
        // Simple friend suggestion: users from same city or with similar interests
        // This could be enhanced with more sophisticated algorithms
        StringBuilder query = new StringBuilder(
                "id != ?1 and status = 'ACTIVE' and id not in (" +
                        "select case when requester.id = ?1 then receiver.id else requester.id end " +
                        "from FriendshipEntity where (requester.id = ?1 or receiver.id = ?1))"
        );

        if (user.cityEntity != null) {
            query.append(" and cityEntity = ?2");
            return UserEntity.find(query.toString(), user, user.cityEntity)
                    .page(0, limit)
                    .list();
        }

        return UserEntity.find(query.toString(), user)
                .page(0, limit)
                .list();
    }

    private FriendDTO convertToDTO(FriendshipEntity friendship, UserEntity currentUser) {
        FriendDTO dto = new FriendDTO();
        dto.id = friendship.id;
        dto.status = friendship.status;
        dto.createdAt = friendship.createdAt;
        dto.acceptedAt = friendship.acceptedAt;
        dto.requestMessage = friendship.requestMessage;

        // Determine which user is the "other" user
        UserEntity otherUser = friendship.getOtherUser(currentUser);
        if (otherUser != null) {
            dto.userId = otherUser.id;
            dto.username = otherUser.username;
            dto.displayName = otherUser.getDisplayName();
            dto.profileImageUrl = otherUser.profileImageUrl;
            dto.organizationType = otherUser.organizationType;

            if (otherUser.isFighter()) {
                dto.primaryDiscipline = otherUser.primaryDiscipline;
            }
        }

        dto.isRequester = friendship.requester.equals(currentUser);

        return dto;
    }
}