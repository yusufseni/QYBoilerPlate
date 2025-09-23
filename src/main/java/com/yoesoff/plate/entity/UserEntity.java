package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.enums.Status;
import com.yoesoff.plate.enums.Themes;
import com.yoesoff.plate.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class UserEntity extends BaseEntity {

    // --- Core identity and authentication ---
    @Column(nullable = false)
    @NotBlank
    public String username;

    @Column(nullable = false)
    public String passwordHash;

    @Column(nullable = false)
    @Email
    public String email;

    // --- Role and organization ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrganizationType organizationType = OrganizationType.PERSONAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserRole role = UserRole.CLIENT;

    // --- Status and theme ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Themes themes = Themes.DARK;

    // --- Profile info ---
    @Column(nullable = true)
    public String firstName;

    @Column(nullable = true)
    public String lastName;

    @Column(nullable = true)
    public LocalDate dateOfBirth;

    @Column(columnDefinition = "TEXT")
    public String bio;

    @Column(nullable = true)
    public String profileImageUrl;

    // --- Contact info ---
    @Column(nullable = true)
    public String phoneNumber;

    // --- Location ---
    @Column(precision = 10, scale = 6)
    public BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    public BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    public CityEntity cityEntity;

    // --- Fighter-specific fields ---
    @Column(nullable = true)
    public String fightName; // Professional fighting name

    @Column(nullable = true)
    public String weightClass;

    @Column(nullable = true)
    public String primaryDiscipline; // MMA, Boxing, Muay Thai, etc.

    @Column(nullable = true)
    public LocalDate professionalDebutDate;

    @Column(nullable = true)
    public String gym;

    @Column(nullable = true)
    public String trainer;

    @Column(columnDefinition = "TEXT")
    public String achievements;

    // --- Social media links ---
    @Column(nullable = true)
    public String instagramUrl;

    @Column(nullable = true)
    public String twitterUrl;

    @Column(nullable = true)
    public String facebookUrl;

    @Column(nullable = true)
    public String youtubeUrl;

    // --- Relationships ---
    @OneToMany(mappedBy = "fighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<FightRecordEntity> fightRecordEntities = new ArrayList<>();

    // --- Constructors ---
    public UserEntity() {}

    public UserEntity(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    // --- Helper methods ---
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }

    public boolean isFighter() {
        return role == UserRole.FIGHTER;
    }

    public String getDisplayName() {
        if (isFighter() && fightName != null && !fightName.isBlank()) {
            return fightName;
        }
        return getFullName();
    }
}