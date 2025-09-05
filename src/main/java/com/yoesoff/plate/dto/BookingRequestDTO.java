package com.yoesoff.plate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingRequestDTO {
    public UUID serviceId;
    public LocalDateTime scheduledDateTime;
    public Integer durationMinutes;
    public String clientNotes;
}