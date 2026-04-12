package com.example.AuthService.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;

    private String role;
    private String college;
    private String company;
}
