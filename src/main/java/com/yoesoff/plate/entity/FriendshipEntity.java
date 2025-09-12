package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.FriendshipStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "friendships", indexes = {
        @Index(columnList = "requester_id, receiver_id", unique = true)
})
public class FriendshipEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    public UserEntity requester; // User who sent friend request

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    public UserEntity receiver; // User who received friend request

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public FriendshipStatus status = FriendshipStatus.PENDING;

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime acceptedAt;
    public LocalDateTime rejectedAt;
    public LocalDateTime blockedAt;

    @Column(columnDefinition = "TEXT")
    public String requestMessage; // Optional message with friend request

    // Static finder methods
    public static List<FriendshipEntity> findFriendsByUser(UserEntity user) {
        return list("(requester = ?1 or receiver = ?1) and status = 'ACCEPTED'", user);
    }

    public static List<FriendshipEntity> findPendingRequestsReceived(UserEntity user) {
        return list("receiver = ?1 and status = 'PENDING'", user);
    }

    public static List<FriendshipEntity> findPendingRequestsSent(UserEntity user) {
        return list("requester = ?1 and status = 'PENDING'", user);
    }

    public static FriendshipEntity findFriendship(UserEntity user1, UserEntity user2) {
        return find("(requester = ?1 and receiver = ?2) or (requester = ?2 and receiver = ?1)",
                user1, user2).firstResult();
    }

    public static boolean areFriends(UserEntity user1, UserEntity user2) {
        FriendshipEntity friendship = findFriendship(user1, user2);
        return friendship != null && friendship.status == FriendshipStatus.ACCEPTED;
    }

    public static long countFriends(UserEntity user) {
        return count("(requester = ?1 or receiver = ?1) and status = 'ACCEPTED'", user);
    }

    public UserEntity getOtherUser(UserEntity currentUser) {
        if (requester.equals(currentUser)) {
            return receiver;
        } else if (receiver.equals(currentUser)) {
            return requester;
        }
        return null;
    }
}