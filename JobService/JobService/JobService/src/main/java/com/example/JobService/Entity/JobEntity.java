package com.example.JobService.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JobEntity {
    @Id
    private String jobId;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String company;
    @Column(nullable = false)
    private String recruiterId;
    @Column(nullable = false)
    @ElementCollection
    private List<String> skills;
    @Column(nullable = false)
    private Integer salary;
    private boolean active;
    @Column(nullable = false)
    private String location;
    private LocalDateTime creationDate;
    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
        this.active = true;
    }
}
