package com.example.ApplicationsService.Config;

import com.example.ApplicationsService.Response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name="user-service",url = "http://userservice:8087")
public interface UserClient {
    @PostMapping("/api/v1/user/internal/bulk")
     Map<String,UserResponse> bulk(@RequestBody List<String> userId);
    @GetMapping("/api/v1/user/internal/{userId}")
     UserResponse getUser(@PathVariable String userId);
}
