package com.example.ApplicationsService.Response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    String userId;
    String username;
    String FirstName;
    String LastName;
    String Email;
    String profilePicture;
    String role;
    String deviceToken;
}
