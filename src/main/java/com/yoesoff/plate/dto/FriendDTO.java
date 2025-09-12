package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.FriendshipStatus;
import com.yoesoff.plate.enums.OrganizationType;

import java.time.LocalDateTime;
import java.util.UUID;

public class FriendDTO {
    public UUID id;
    public FriendshipStatus status;
    public LocalDateTime createdAt;
    public LocalDateTime acceptedAt;
    public String requestMessage;

    // Friend's info
    public UUID userId;
    public String username;
    public String displayName;
    public String profileImageUrl;
    public OrganizationType organizationType;
    public String primaryDiscipline; // For fighters

    // Request context
    public boolean isRequester; // true if current user sent the request
}
