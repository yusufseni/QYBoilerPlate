/**
 * Base entity for all Entities in the IFighter platform.
 * Provides common fields and behavior.
 * Path: src/main/java/com/yoesoff/plate/entity/BaseEntity.java
 *
 * @author IFighter Development Team
 * @version 1.0
 * @since 2025-09-15
 */
package com.yoesoff.plate.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity extends PanacheEntityBase {

    @Version
    private Long version;

    @Id
    @GeneratedValue
    @UuidGenerator
    public UUID id;

    @CreationTimestamp
    @Column(nullable = true, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = true)
    public LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + id + ")";
    }

}