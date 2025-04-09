package com.example.trace.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String provider;

    private String email;

    private String nickname;

    private String profileImage;

    // Add other fields as needed
}