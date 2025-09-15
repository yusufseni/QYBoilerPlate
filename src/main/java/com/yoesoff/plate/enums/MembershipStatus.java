/**
 * Enumeration of possible membership statuses in the IFighter platform.
 * Represents the lifecycle of a membership from request to termination.
 * Path: src/main/java/com/yoesoff/plate/enums/MembershipStatus.java
 */
package com.yoesoff.plate.enums;

public enum MembershipStatus {
    /** Membership request submitted, awaiting organization approval */
    PENDING("Pending Approval"),

    /** Membership approved and currently active */
    ACTIVE("Active"),

    /** Membership temporarily suspended (can be reactivated) */
    SUSPENDED("Suspended"),

    /** Membership has reached its end date */
    EXPIRED("Expired"),

    /** Membership cancelled by either member or organization */
    CANCELLED("Cancelled");

    private final String displayName;

    MembershipStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this status represents a terminated membership.
     * @return true if the membership is no longer active
     */
    public boolean isTerminated() {
        return this == EXPIRED || this == CANCELLED;
    }

    /**
     * Checks if this status allows the membership to be reactivated.
     * @return true if the membership can be reactivated
     */
    public boolean canBeReactivated() {
        return this == SUSPENDED;
    }
}