package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.MembershipType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class MembershipRequestDTO {
    public UUID organizationId;
    public MembershipType membershipType;
    public LocalDate startDate;
    public LocalDate endDate; // Optional, null for lifetime
    public BigDecimal monthlyFee;
    public String memberNotes;
}