package com.example.ApplicationsService.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String applicationId;
    @Column(nullable = false)
    private String jobId;
    @Column(nullable = false)
    private String userId;
    private String status;
    @Column(nullable = true)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> refreerId;
    @Column(nullable = false)
    private  String resumeUrl;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> skills;
    private boolean referred;
    private Integer priority;

    private LocalDateTime applied;
    @PrePersist
    public void prePersist() {
        this.applied = LocalDateTime.now();
        this.priority=0;
        this.referred=false;
        this.status="APPLIED";
    }
}
