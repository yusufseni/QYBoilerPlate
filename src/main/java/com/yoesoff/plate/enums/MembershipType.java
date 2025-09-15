/**
 * Enumeration of membership types available in the IFighter platform.
 * Different types may have different benefits, pricing, and access levels.
 */
package com.yoesoff.plate.enums;

public enum MembershipType {
    BASIC("Basic Membership", 1),
    PREMIUM("Premium Membership", 2),
    VIP("VIP Membership", 3),
    STUDENT("Student Membership", 1),
    CORPORATE("Corporate Membership", 2),
    LIFETIME("Lifetime Membership", 3);

    private final String displayName;
    private final int tier; // For comparing membership levels

    MembershipType(String displayName, int tier) {
        this.displayName = displayName;
        this.tier = tier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getTier() {
        return tier;
    }

    /**
     * Checks if this membership type is higher tier than another.
     * @param other The membership type to compare against
     * @return true if this type has a higher tier
     */
    public boolean isHigherTierThan(MembershipType other) {
        return this.tier > other.tier;
    }
}