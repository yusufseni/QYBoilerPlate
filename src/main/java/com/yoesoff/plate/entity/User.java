package com.yoesoff.plate.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class User extends PanacheEntity {
    public String username;
    public String password;
    public String email;

    // Default constructor
    public User() {
    }

    // Constructor with parameters
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and setters can be added if needed
}
