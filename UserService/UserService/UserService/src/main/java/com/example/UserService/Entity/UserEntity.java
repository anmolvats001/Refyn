package com.example.UserService.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntity {
    @Id
    String userId;
    @Column(unique = true)
    String username;
    @Column(nullable = false)
    String firstName;
    String lastName;
    @Column(unique = true)
    String email;
    String role;
    String company;
    String college;
    String profilePicture;
    Boolean active;
    String resumeUrl;
    String deviceToken;
    @ElementCollection
    List<String> skills;
    Double cgpa;
    Integer graduationYear;
    String designation;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        profilePicture="https://cdn-icons-png.flaticon.com/512/149/149071.png";
        active=true;
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}