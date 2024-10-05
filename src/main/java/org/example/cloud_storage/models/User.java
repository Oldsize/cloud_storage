package org.example.cloud_storage.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Cacheable
@Data
@Table(schema = "public", name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name = "password", unique = true, nullable = false, updatable = false)
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }
}