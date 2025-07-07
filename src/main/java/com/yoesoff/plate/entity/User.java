package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.Status;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "app_users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    public String username;
    public String password;
    public String email;
    public Status status;

    // Default constructor
    public User() {
    }

    // Constructor with parameters
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = Status.Banned; // Default status
    }

    // Getters and setters can be added if needed
}
