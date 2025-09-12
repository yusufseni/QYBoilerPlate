// MembershipDTO.java
package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.OrganizationType;

import java.util.UUID;

public class OrganizationSummaryDTO {
    public UUID id;
    public String username;
    public String displayName;
    public String bio;
    public OrganizationType organizationType;
    public String profileImageUrl;
    public String cityName;
    public long memberCount;
    public boolean hasActiveMembership; // For current user
    public boolean hasPendingMembership; // For current user
}