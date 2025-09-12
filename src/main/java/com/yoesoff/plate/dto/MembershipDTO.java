package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.MembershipStatus;
import com.yoesoff.plate.enums.MembershipType;
import com.yoesoff.plate.enums.OrganizationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class MembershipDTO {
    public UUID id;
    public MembershipType membershipType;
    public MembershipStatus status;
    public LocalDate startDate;
    public LocalDate endDate;
    public BigDecimal monthlyFee;
    public String benefits;
    public String memberNotes;
    public String organizationNotes;
    public LocalDateTime createdAt;
    public LocalDateTime approvedAt;
    public LocalDateTime suspendedAt;
    public LocalDateTime cancelledAt;

    // Member info
    public String memberName;
    public String memberUsername;

    // Organization info
    public String organizationName;
    public String organizationUsername;
    public OrganizationType organizationType;

    public boolean isActive() {
        if (status != MembershipStatus.ACTIVE) return false;
        if (endDate != null && endDate.isBefore(LocalDate.now())) return false;
        return true;
    }

    public boolean isExpiringSoon() {
        if (endDate == null) return false;
        return endDate.isBefore(LocalDate.now().plusDays(30));
    }
}