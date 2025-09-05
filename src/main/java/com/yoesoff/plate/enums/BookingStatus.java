package com.yoesoff.plate.enums;

public enum BookingStatus {
    PENDING,    // Awaiting fighter confirmation
    CONFIRMED,  // Fighter confirmed
    CANCELLED,  // Cancelled by either party
    COMPLETED,  // Session completed
    NO_SHOW     // Client didn't show up
}