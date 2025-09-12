package com.yoesoff.plate.enums;

public enum MembershipType {
    BASIC("Basic Membership"),
    PREMIUM("Premium Membership"),
    VIP("VIP Membership"),
    STUDENT("Student Membership"),
    CORPORATE("Corporate Membership"),
    LIFETIME("Lifetime Membership");

    private final String displayName;

    MembershipType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
