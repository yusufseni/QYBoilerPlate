package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.ServiceType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fighter_services")
public class FighterServiceEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fighter_id")
    public User fighter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ServiceType serviceType;

    @Column(nullable = false)
    @NotBlank
    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @DecimalMin("0.00")
    public BigDecimal pricePerHour;

    public Integer durationMinutes; // Default session duration

    public boolean isActive = true;
    public boolean isOnline = false; // Online/remote service option

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Booking> bookings = new ArrayList<>();

    // Static finder methods
    public static List<FighterServiceEntity> findByServiceType(ServiceType type) {
        return list("serviceType = ?1 and isActive = true", type);
    }

    public static List<FighterServiceEntity> findByFighter(User fighter) {
        return list("fighter = ?1 and isActive = true", fighter);
    }
}