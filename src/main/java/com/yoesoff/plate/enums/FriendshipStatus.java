/**
 * Enumeration of possible friendship statuses in the IFighter platform.
 */
package com.yoesoff.plate.enums;

public enum FriendshipStatus {
    /** Friend request sent, awaiting response */
    PENDING("Pending"),

    /** Friend request accepted, users are now friends */
    ACCEPTED("Friends"),

    /** Friend request was rejected */
    REJECTED("Rejected"),

    /** One user has blocked the other */
    BLOCKED("Blocked");

    private final String displayName;

    FriendshipStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this status represents an active friendship.
     * @return true if users are friends
     */
    public boolean isFriendship() {
        return this == ACCEPTED;
    }

    /**
     * Checks if this status prevents further interaction.
     * @return true if interaction is blocked
     */
    public boolean preventsInteraction() {
        return this == BLOCKED;
    }
}