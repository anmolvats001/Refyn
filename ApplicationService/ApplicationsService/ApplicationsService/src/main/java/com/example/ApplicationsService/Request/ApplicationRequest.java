package com.example.ApplicationsService.Request;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRequest {
    private String resumeUrl;
    private List<String> skills;

}
