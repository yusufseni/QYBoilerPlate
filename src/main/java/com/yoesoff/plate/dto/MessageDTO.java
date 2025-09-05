package com.yoesoff.plate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDTO {
    public UUID id;
    public String content;
    public UUID senderId;
    public String senderName;
    public UUID recipientId;
    public String recipientName;
    public boolean isRead;
    public LocalDateTime sentAt;
    public UUID bookingId; // if related to a booking
}