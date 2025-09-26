package com.marshallchikari.taskmanager.taskservice.dto;

import java.time.LocalDateTime;

public class AuthUserDTO {
    private Long id;
    private String username;
    private LocalDateTime createdAt;

    public AuthUserDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
