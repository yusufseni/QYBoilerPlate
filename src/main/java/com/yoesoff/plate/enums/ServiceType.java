// Service Type Enum
package com.yoesoff.plate.enums;

public enum ServiceType {
    PERSONAL_TRAINING("Personal Training"),
    GROUP_TRAINING("Group Training"),
    TECHNIQUE_COACHING("Technique Coaching"),
    FITNESS_TRAINING("Fitness Training"),
    NUTRITION_CONSULTING("Nutrition Consulting"),
    BODYGUARD_SERVICE("Bodyguard Service"),
    SELF_DEFENSE_TRAINING("Self Defense Training"),
    COMPETITION_PREP("Competition Preparation"),
    ONLINE_COACHING("Online Coaching");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}