package com.example.trace.admin.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private String providerId; //provider에서 받아온 userId

    @Column(nullable = false)
    private String provider;

    private String email;

    private String nickname;

    private String profileImage;

    private String password;
    private String username;
    private String role;

    public List<String> getRoleList() {
        if(this.role != null && !this.role.isEmpty()) {
            return Arrays.asList(this.role.split(","));
        }
        return new ArrayList<>();
    }

}