package com.example.ApplicationsService.Response;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ApplicationResponse {
    private String applicationId;
    private String jobId;
    private String status;
    private List<String> refreerId;
    private String resumeUrl;
    private List<String> skills;
    private boolean referred;
    private Integer priority;
    String userId;
    String username;
    String firstName;
    String lastName;
    String email;
    String profilePicture;
    String company;
    String title;
    String description;
}
