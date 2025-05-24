package com.example.trace.mission.mission;

import jakarta.persistence.*;

@Entity
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;


    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}