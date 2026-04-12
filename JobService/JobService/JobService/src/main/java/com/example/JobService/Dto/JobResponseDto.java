package com.example.JobService.Dto;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponseDto {
    private String jobId;
    private String title;
    private String description;
    private String category;
    private String company;
    private String recruiterId;
    private List<String> skills;
    private Integer salary;
    private boolean active;
    private String location;
    private LocalDateTime creationDate;
    private Boolean applied;
}