package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.BookingStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class BookingEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    public FighterServiceEntity service;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    public UserEntity client;

    @Column(nullable = false)
    @NotNull
    public LocalDateTime scheduledDateTime;

    public Integer durationMinutes;

    @Column(precision = 10, scale = 2)
    public BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public BookingStatus status = BookingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    public String clientNotes; // Notes from client

    @Column(columnDefinition = "TEXT")
    public String fighterNotes; // Notes from fighter

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime confirmedAt;
    public LocalDateTime cancelledAt;
    public LocalDateTime completedAt;

    // Getters for fighter convenience
    public UserEntity getFighter() {
        return service != null ? service.fighter : null;
    }
}