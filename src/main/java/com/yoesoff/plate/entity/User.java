package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.Status;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

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

    @Column(nullable = false)
    public String username;

    @Column(nullable = false)
    public String passwordHash; // simpan HASH, bukan plaintext

    @Column(nullable = false)
    public String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.Active;

    public User() {
    }

    public User(String username, String aDefault, String email) {
        this.username = username;
        this.passwordHash = aDefault;
        this.email = email;
    }
}
