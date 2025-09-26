package com.marshallchikari.taskmanager.authservice.dto;

import java.time.LocalDateTime;

public class UserResponseDTO {
    private Long id;
    private String username;
    private LocalDateTime created_at;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

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
        return created_at;
   }

   public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
   }
}
