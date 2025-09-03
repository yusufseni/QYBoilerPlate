package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.enums.Status;
import com.yoesoff.plate.enums.Themes;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "app_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrganizationType organizationType = OrganizationType.PERSONAL;

    @Column(nullable = false)
    public String username;

    @Column(nullable = false)
    public String passwordHash; // simpan HASH, bukan plaintext

    @Column(nullable = false)
    public String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Themes themes = Themes.DARK;

    // Koordinat lokasi (nullable)
    @Column(precision = 10, scale = 6, nullable = true)
    public BigDecimal latitude;

    @Column(precision = 10, scale = 6, nullable = true)
    public BigDecimal longitude;

    public User() {
    }

    public User(String username, String aDefault, String email) {
        this.username = username;
        this.passwordHash = aDefault;
        this.email = email;
    }
}
