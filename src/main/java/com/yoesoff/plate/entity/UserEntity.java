package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.enums.Status;
import com.yoesoff.plate.enums.Themes;
import com.yoesoff.plate.enums.UserRole;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrganizationType organizationType = OrganizationType.PERSONAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserRole role = UserRole.CLIENT;

    @Column(nullable = false)
    @NotBlank
    public String username;

    @Column(nullable = false)
    public String passwordHash;

    @Column(nullable = false)
    @Email
    public String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Themes themes = Themes.DARK;

    // Basic profile info
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public LocalDate dateOfBirth;

    @Column(columnDefinition = "TEXT")
    public String bio;

    public String profileImageUrl;

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime updatedAt = LocalDateTime.now();

    // Location coordinates
    @Column(precision = 10, scale = 6)
    public BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    public BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    public CityEntity cityEntity;

    // Fighter-specific fields (only populated if role = FIGHTER)
    public String fightName; // Professional fighting name
    public String weightClass;
    public String primaryDiscipline; // MMA, Boxing, Muay Thai, etc.
    public LocalDate professionalDebutDate;
    public String gym;
    public String trainer;

    @Column(columnDefinition = "TEXT")
    public String achievements;

    // Social media links
    public String instagramUrl;
    public String twitterUrl;
    public String facebookUrl;
    public String youtubeUrl;

    // One-to-Many relationships for fighters
    @OneToMany(mappedBy = "fighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<FightRecordEntity> fightRecordEntities = new ArrayList<>();

    @OneToMany(mappedBy = "fighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<FighterServiceEntity> services = new ArrayList<>();

    @OneToMany(mappedBy = "fighter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ReviewEntity> receivedReviews = new ArrayList<>();

    // Constructors
    public UserEntity() {}

    public UserEntity(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    // Helper methods
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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}