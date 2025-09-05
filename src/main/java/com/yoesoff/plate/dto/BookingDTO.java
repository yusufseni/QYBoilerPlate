// BookingDTO
package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.BookingStatus;
import com.yoesoff.plate.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingDTO {
    public UUID id;
    public LocalDateTime scheduledDateTime;
    public Integer durationMinutes;
    public BigDecimal totalPrice;
    public BookingStatus status;
    public String clientNotes;
    public String fighterNotes;
    public LocalDateTime createdAt;
    public LocalDateTime confirmedAt;
    public LocalDateTime cancelledAt;
    public LocalDateTime completedAt;

    // Service info
    public String serviceTitle;
    public ServiceType serviceType;

    // Client info
    public String clientName;
    public String clientUsername;

    // Fighter info
    public String fighterName;
    public String fighterUsername;
}