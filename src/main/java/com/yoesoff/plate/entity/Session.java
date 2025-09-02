package com.yoesoff.plate.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions", indexes = {
        @Index(columnList = "token", unique = true)
})
public class Session extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public User user;

    @Column(nullable = false, unique = true, length = 64)
    public String token;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    @Column(nullable = false)
    public Instant expiresAt; // mis. now + 7 hari
}
