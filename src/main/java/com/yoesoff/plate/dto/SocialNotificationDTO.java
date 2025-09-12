package com.yoesoff.plate.dto;

public class SocialNotificationDTO {
    public enum Type {
        FRIEND_REQUEST_RECEIVED,
        FRIEND_REQUEST_ACCEPTED,
        MEMBERSHIP_REQUEST_RECEIVED,
        MEMBERSHIP_APPROVED,
        MEMBERSHIP_REJECTED,
        MEMBERSHIP_SUSPENDED,
        MEMBERSHIP_EXPIRING
    }
}
