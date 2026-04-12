package com.example.UserService.Controller;

import com.example.UserService.Entity.UserEntity;
import com.example.UserService.Repo.UserRepo;
import com.example.UserService.Service.UserService;
import com.example.UserService.UserDto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    public UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserEntity> me(JwtAuthenticationToken token){
        String id=userService.getByToken(token);
        if(id==null){
            return ResponseEntity.badRequest().build();
        }
        UserEntity user=userService.getProfile(id);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/internal/bulk")
    public Map<String, UserResponse> bulk(@RequestBody List<String> ids){
        return userService.getUsers(ids);
    }
    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.token.subject")
    public ResponseEntity<UserEntity> getUser(@PathVariable String userId){
        UserEntity userData=userService.getProfile(userId);
        if(userData!=null){
            return ResponseEntity.ok(userData);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/internal/{userId}")
    public UserResponse getUserDataForFeign(@PathVariable String userId){
        UserResponse userData=userService.getProfileForFeign(userId);
            return userData;
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserEntity> getAllUsers(){
        List<UserEntity>list =userService.getAllStudents();
        return list;
    }
    @PatchMapping("/{userId}")
    @PreAuthorize("#userId == authentication.token.subject or hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody  UserEntity user){
        UserEntity userData=userService.updateStudentProfile(userId,user);
        if(userData!=null){
            return ResponseEntity.ok("User Data Updated");
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.token.subject or hasRole('ADMIN')" )
    public ResponseEntity<String> deleteUser(@PathVariable String userId){
       Boolean val= userService.deleteStudentProfile(userId);
       if(val==true){
           return ResponseEntity.ok("Deleted Successfully");
       }
       else{
           return ResponseEntity.badRequest().build();
       }
    }
    @PatchMapping("/change-device-token")
    public ResponseEntity<String> changeDeviceToken(JwtAuthenticationToken jwtAuthenticationToken,@RequestBody String deviceToken){
        try {
            String userId= jwtAuthenticationToken.getPrincipal().toString();
            userService.changeDeviceToken(userId,deviceToken);
            return ResponseEntity.ok("Device Token Updated");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
