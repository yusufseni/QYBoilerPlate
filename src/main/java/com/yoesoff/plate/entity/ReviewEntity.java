package com.yoesoff.plate.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class ReviewEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fighter_id")
    public UserEntity fighter;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    public UserEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    public BookingEntity bookingEntity;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    public Integer rating;

    @Column(columnDefinition = "TEXT")
    public String comment;

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    // Static methods for rating calculations
    public static Double getAverageRating(UserEntity fighter) {
        return find("SELECT AVG(CAST(rating as double)) FROM Review WHERE fighter = ?1", fighter)
                .project(Double.class)
                .firstResult();
    }

    public static Long getReviewCount(UserEntity fighter) {
        return count("fighter = ?1", fighter);
    }
}