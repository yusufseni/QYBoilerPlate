/**
 * Entity representing friendship relationships between users.
 * Friendships are mutual relationships that must be accepted by both parties.
 *
 * Business Rules:
 * - Any user can send friend requests to any other user
 * - Friendships are bidirectional once accepted
 * - Users cannot send multiple requests to the same person
 * - Blocked relationships prevent future friend requests
 *
 * @see com.yoesoff.plate.enums.FriendshipStatus
 */
package com.yoesoff.plate.entity.social;

import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.FriendshipStatus;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "friendships",
        indexes = {
                @Index(name = "idx_friendship_requester", columnList = "requester_id"),
                @Index(name = "idx_friendship_receiver", columnList = "receiver_id"),
                @Index(name = "idx_friendship_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_friendship_users",
                        columnNames = {"requester_id", "receiver_id"})
        })
public class FriendshipEntity extends BaseSocialRelationship {

    /** User who initiated the friend request */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @NotNull
    public UserEntity requester;

    /** User who received the friend request */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @NotNull
    public UserEntity receiver;

    /** Current status of the friendship */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    public FriendshipStatus status = FriendshipStatus.PENDING;

    /** Optional message sent with the friend request */
    @Column(name = "request_message", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Request message cannot exceed 1000 characters")
    public String requestMessage;

    // Lifecycle timestamps
    public LocalDateTime acceptedAt;
    public LocalDateTime rejectedAt;
    public LocalDateTime blockedAt;

    @Override
    protected void validateRelationship() {
        if (requester == null || receiver == null) {
            throw new IllegalStateException("Requester and receiver must be specified");
        }

        if (requester.equals(receiver)) {
            throw new IllegalArgumentException("User cannot create friendship with themselves");
        }
    }

    @Override
    public boolean isActive() {
        return status == FriendshipStatus.ACCEPTED;
    }

    /**
     * Gets the "other" user in this friendship relationship relative to the given user.
     * @param currentUser The reference user
     * @return The other user in the friendship, or null if currentUser is not part of this friendship
     */
    public UserEntity getOtherUser(UserEntity currentUser) {
        if (requester.equals(currentUser)) {
            return receiver;
        } else if (receiver.equals(currentUser)) {
            return requester;
        }
        return null;
    }

    /**
     * Checks if the given user is the one who initiated this friendship request.
     * @param user The user to check
     * @return true if the user is the requester
     */
    public boolean isRequester(UserEntity user) {
        return requester.equals(user);
    }

    @PrePersist
    @PreUpdate
    protected void validate() {
        validateRelationship();
    }

    // ==================== STATIC QUERY METHODS ====================

    /**
     * Finds all accepted friendships for a user.
     * @param user The user whose friends to find
     * @return List of friendship entities where the user is involved and status is ACCEPTED
     */
    public static List<FriendshipEntity> findFriendsByUser(UserEntity user) {
        return list("(requester = ?1 or receiver = ?1) and status = ?2",
                Sort.by("acceptedAt").descending(), user, FriendshipStatus.ACCEPTED);
    }

    /**
     * Finds pending friend requests received by a user.
     * @param user The user who received the requests
     * @return List of pending friendship requests
     */
    public static List<FriendshipEntity> findPendingRequestsReceived(UserEntity user) {
        return list("receiver = ?1 and status = ?2",
                Sort.by("createdAt").descending(), user, FriendshipStatus.PENDING);
    }

    /**
     * Finds pending friend requests sent by a user.
     * @param user The user who sent the requests
     * @return List of sent pending requests
     */
    public static List<FriendshipEntity> findPendingRequestsSent(UserEntity user) {
        return list("requester = ?1 and status = ?2",
                Sort.by("createdAt").descending(), user, FriendshipStatus.PENDING);
    }

    /**
     * Finds any friendship relationship between two users (regardless of who initiated it).
     * @param user1 First user
     * @param user2 Second user
     * @return Optional containing the friendship if one exists
     */
    public static Optional<FriendshipEntity> findFriendshipBetween(UserEntity user1, UserEntity user2) {
        return find("(requester = ?1 and receiver = ?2) or (requester = ?2 and receiver = ?1)",
                user1, user2).firstResultOptional();
    }

    /**
     * Checks if two users are friends (have an accepted friendship).
     * @param user1 First user
     * @param user2 Second user
     * @return true if users are friends
     */
    public static boolean areFriends(UserEntity user1, UserEntity user2) {
        return findFriendshipBetween(user1, user2)
                .map(friendship -> friendship.status == FriendshipStatus.ACCEPTED)
                .orElse(false);
    }

    /**
     * Counts the number of friends for a user.
     * @param user The user whose friends to count
     * @return Number of accepted friendships
     */
    public static long countFriends(UserEntity user) {
        return count("(requester = ?1 or receiver = ?1) and status = ?2",
                user, FriendshipStatus.ACCEPTED);
    }

    /**
     * Checks if one user has blocked another.
     * @param user1 Potentially blocking user
     * @param user2 Potentially blocked user
     * @return true if user1 has blocked user2
     */
    public static boolean isBlocked(UserEntity user1, UserEntity user2) {
        return findFriendshipBetween(user1, user2)
                .map(friendship -> friendship.status == FriendshipStatus.BLOCKED)
                .orElse(false);
    }
}