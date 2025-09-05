package com.yoesoff.plate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationDTO {
    public UUID id;
    public String title;
    public String message;
    public String type; // BOOKING_CONFIRMED, BOOKING_CANCELLED, etc.
    public boolean isRead;
    public LocalDateTime createdAt;
    public UUID relatedEntityId; // booking ID, service ID, etc.
}