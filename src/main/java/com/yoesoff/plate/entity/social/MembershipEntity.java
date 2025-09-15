/**
 * Entity representing membership relationships between personal users and organizations.
 * A membership allows a personal user to be associated with an organization (gym, dojo, etc.)
 * with specific terms, benefits, and duration.
 * Path: src/main/java/com/yoesoff/plate/entity/social/MembershipEntity.java
 *
 * Business Rules:
 * - Only PERSONAL users can become members of organizations
 * - One user can have only one active membership per organization
 * - Memberships have lifecycle: PENDING -> ACTIVE -> [SUSPENDED/EXPIRED/CANCELLED]
 *
 * @see com.yoesoff.plate.enums.MembershipStatus
 * @see com.yoesoff.plate.enums.MembershipType
 */
package com.yoesoff.plate.entity.social;

import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.MembershipStatus;
import com.yoesoff.plate.enums.MembershipType;
import com.yoesoff.plate.enums.OrganizationType;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "memberships",
        indexes = {
                @Index(name = "idx_membership_member", columnList = "member_id"),
                @Index(name = "idx_membership_org", columnList = "organization_id"),
                @Index(name = "idx_membership_status", columnList = "status"),
                @Index(name = "idx_membership_dates", columnList = "start_date, end_date")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_organization",
                        columnNames = {"member_id", "organization_id"})
        })
public class MembershipEntity extends BaseSocialRelationship {

    /** The personal user who holds this membership */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull
    public UserEntity member;

    /** The organization that grants this membership */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @NotNull
    public UserEntity organization;

    /** Type/tier of membership (Basic, Premium, VIP, etc.) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    public MembershipType membershipType;

    /** Current status of the membership */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    public MembershipStatus status = MembershipStatus.PENDING;

    /** Date when membership becomes effective */
    @Column(name = "start_date", nullable = false)
    @NotNull
    public LocalDate startDate;

    /** Date when membership expires (null = lifetime membership) */
    @Column(name = "end_date")
    public LocalDate endDate;

    /** Monthly fee for this membership (null = free) */
    @Column(precision = 12, scale = 2)
    @Positive
    public BigDecimal monthlyFee;

    /** Description of membership benefits and privileges */
    @Column(columnDefinition = "TEXT")
    public String benefits;

    /** Notes/comments from the member */
    @Column(name = "member_notes", columnDefinition = "TEXT")
    public String memberNotes;

    /** Notes/comments from the organization */
    @Column(name = "organization_notes", columnDefinition = "TEXT")
    public String organizationNotes;

    // Lifecycle timestamps
    public LocalDateTime approvedAt;
    public LocalDateTime suspendedAt;
    public LocalDateTime cancelledAt;

    @Override
    protected void validateRelationship() {
        if (member == null || organization == null) {
            throw new IllegalStateException("Member and organization must be specified");
        }

        if (member.organizationType != OrganizationType.PERSONAL) {
            throw new IllegalArgumentException("Only personal users can have memberships");
        }

        if (organization.organizationType == OrganizationType.PERSONAL) {
            throw new IllegalArgumentException("Cannot create membership with personal user as organization");
        }

        if (member.equals(organization)) {
            throw new IllegalArgumentException("User cannot create membership with themselves");
        }

        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    @Override
    public boolean isActive() {
        if (status != MembershipStatus.ACTIVE) {
            return false;
        }

        LocalDate today = LocalDate.now();
        if (startDate.isAfter(today)) {
            return false; // Not yet started
        }

        return endDate == null || !endDate.isBefore(today); // null endDate = lifetime
    }

    /**
     * Checks if membership is expiring within the specified number of days.
     * @param days Number of days to check ahead
     * @return true if membership expires within the specified days
     */
    public boolean isExpiringWithin(int days) {
        if (endDate == null) {
            return false; // Lifetime membership never expires
        }
        return endDate.isBefore(LocalDate.now().plusDays(days));
    }

    @PrePersist
    @PreUpdate
    protected void validate() {
        validateRelationship();
    }

    // ==================== STATIC QUERY METHODS ====================

    /**
     * Finds all memberships for a specific member.
     * @param member The member user
     * @return List of memberships ordered by creation date (newest first)
     */
    public static List<MembershipEntity> findByMember(UserEntity member) {
        return list("member = ?1", Sort.by("createdAt").descending(), member);
    }

    /**
     * Finds all active memberships for a specific member.
     * @param member The member user
     * @return List of active memberships
     */
    public static List<MembershipEntity> findActiveMembershipsByMember(UserEntity member) {
        return list("member = ?1 and status = ?2 and (endDate is null or endDate >= ?3)",
                member, MembershipStatus.ACTIVE, LocalDate.now());
    }

    /**
     * Finds all members of a specific organization.
     * @param organization The organization user
     * @return List of memberships for the organization
     */
    public static List<MembershipEntity> findByOrganization(UserEntity organization) {
        return list("organization = ?1", Sort.by("createdAt").descending(), organization);
    }

    /**
     * Finds active members of a specific organization.
     * @param organization The organization user
     * @return List of active memberships
     */
    public static List<MembershipEntity> findActiveMembersByOrganization(UserEntity organization) {
        return list("organization = ?1 and status = ?2 and (endDate is null or endDate >= ?3)",
                organization, MembershipStatus.ACTIVE, LocalDate.now());
    }

    /**
     * Finds existing membership between a member and organization.
     * @param member The member user
     * @param organization The organization user
     * @return Optional containing the membership if found
     */
    public static Optional<MembershipEntity> findMembership(UserEntity member, UserEntity organization) {
        return find("member = ?1 and organization = ?2", member, organization)
                .firstResultOptional();
    }

    /**
     * Finds memberships by status across all users.
     * @param status The membership status to filter by
     * @return List of memberships with the specified status
     */
    public static List<MembershipEntity> findByStatus(MembershipStatus status) {
        return list("status = ?1", Sort.by("createdAt").descending(), status);
    }

    /**
     * Finds memberships expiring within specified days.
     * @param days Number of days to look ahead
     * @return List of memberships expiring soon
     */
    public static List<MembershipEntity> findExpiringWithin(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return list("status = ?1 and endDate is not null and endDate <= ?2 and endDate >= ?3",
                MembershipStatus.ACTIVE, cutoffDate, LocalDate.now());
    }
}