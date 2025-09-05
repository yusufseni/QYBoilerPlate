package com.yoesoff.plate.entity;

import com.yoesoff.plate.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fighter_id")
    public User fighter;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    public User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    public Booking booking;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    public Integer rating;

    @Column(columnDefinition = "TEXT")
    public String comment;

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    // Static methods for rating calculations
    public static Double getAverageRating(User fighter) {
        return find("SELECT AVG(CAST(rating as double)) FROM Review WHERE fighter = ?1", fighter)
                .project(Double.class)
                .firstResult();
    }

    public static Long getReviewCount(User fighter) {
        return count("fighter = ?1", fighter);
    }
}