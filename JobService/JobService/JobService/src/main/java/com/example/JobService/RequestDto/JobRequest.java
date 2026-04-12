package com.example.JobService.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private String title;
    private String description;
    private String category;
    private String company;
    private List<String> skills;
    private Integer salary;
    private String location;
    private LocalDateTime creationDate;
}
