package com.example.UserService.UserDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataDto {
    String userId;
    String username;
    String firstName;
    String lastName;
    String email;
    String role;
    String company;
    String college;
}
