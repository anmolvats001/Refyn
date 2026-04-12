package com.example.AuthService.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String userId;
    private String role;
    private String college;
    private String company;
}
