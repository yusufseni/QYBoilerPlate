package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.MembershipStatus;
import com.yoesoff.plate.enums.MembershipType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "memberships", indexes = {
        @Index(columnList = "member_id, organization_id", unique = true)
})
public class MembershipEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    public UserEntity member; // Personal user who becomes member

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    public UserEntity organization; // Organization user

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MembershipType membershipType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MembershipStatus status = MembershipStatus.PENDING;

    @Column(nullable = false)
    @NotNull
    public LocalDate startDate;

    public LocalDate endDate; // null for lifetime memberships

    @Column(precision = 10, scale = 2)
    public BigDecimal monthlyFee;

    @Column(columnDefinition = "TEXT")
    public String benefits; // Description of membership benefits

    @Column(columnDefinition = "TEXT")
    public String memberNotes; // Notes from member

    @Column(columnDefinition = "TEXT")
    public String organizationNotes; // Notes from organization

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime approvedAt;
    public LocalDateTime suspendedAt;
    public LocalDateTime cancelledAt;

    // Static finder methods
    public static List<MembershipEntity> findByMember(UserEntity member) {
        return list("member = ?1 and status != 'CANCELLED'", member);
    }

    public static List<MembershipEntity> findByOrganization(UserEntity organization) {
        return list("organization = ?1 and status != 'CANCELLED'", organization);
    }

    public static List<MembershipEntity> findActiveByMember(UserEntity member) {
        return list("member = ?1 and status = 'ACTIVE'", member);
    }

    public static MembershipEntity findByMemberAndOrganization(UserEntity member, UserEntity organization) {
        return find("member = ?1 and organization = ?2", member, organization).firstResult();
    }

    public boolean isActive() {
        if (status != MembershipStatus.ACTIVE) return false;
        if (endDate != null && endDate.isBefore(LocalDate.now())) return false;
        return true;
    }
}