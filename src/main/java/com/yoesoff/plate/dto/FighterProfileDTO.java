package com.yoesoff.plate.dto;

import java.util.UUID;

public class FighterProfileDTO {
    public UUID id;
    public String username;
    public String fightName;
    public String firstName;
    public String lastName;
    public String bio;
    public String primaryDiscipline;
    public String weightClass;
    public String gym;
    public String profileImageUrl;
    public String cityName;
    public Double averageRating;
    public Long reviewCount;
    public Long servicesCount;

    public String getDisplayName() {
        if (fightName != null && !fightName.isBlank()) {
            return fightName;
        }
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }
}