package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.enums.Status;
import com.yoesoff.plate.enums.Themes;
import com.yoesoff.plate.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO {
    public UUID id;
    public OrganizationType organizationType;
    public UserRole role;
    public String username;
    public String email;
    public Status status;
    public Themes themes;

    // Profile fields
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public LocalDate dateOfBirth;
    public String bio;
    public String profileImageUrl;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    // Location
    public BigDecimal latitude;
    public BigDecimal longitude;
    public String cityName;

    // Fighter-specific fields
    public String fightName;
    public String weightClass;
    public String primaryDiscipline;
    public LocalDate professionalDebutDate;
    public String gym;
    public String trainer;
    public String achievements;

    // Social media
    public String instagramUrl;
    public String twitterUrl;
    public String facebookUrl;
    public String youtubeUrl;

    // Stats (computed fields)
    public Double averageRating;
    public Long reviewCount;
    public Long servicesCount;
    public String fightRecord; // e.g., "10-2-1"

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }

    public String getDisplayName() {
        if (role == UserRole.FIGHTER && fightName != null && !fightName.isBlank()) {
            return fightName;
        }
        return getFullName();
    }
}