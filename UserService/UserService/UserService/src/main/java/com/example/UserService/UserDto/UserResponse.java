package com.example.UserService.UserDto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    String userId;
    String username;
    String firstName;
    String lastName;
    String email;
    String profilePicture;
    String role;
    String deviceToken;
}
