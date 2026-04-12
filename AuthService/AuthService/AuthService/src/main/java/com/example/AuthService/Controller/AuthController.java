package com.example.AuthService.Controller;

import com.example.AuthService.Dto.RequestDto;
import com.example.AuthService.Dto.ResponseDto;
import com.example.AuthService.Service.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    KeycloakService keycloakService;
    @PostMapping("/register")
    public String register(@RequestBody RequestDto requestDto) {

        String userId= keycloakService.createUser(requestDto);
        System.out.println(userId);
        return userId;
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody RequestDto requestDto) {
        ResponseDto responseDto= keycloakService.login(requestDto.getUsername(),requestDto.getPassword());
        if(responseDto.getToken()==null){
            return ResponseEntity.badRequest().build();
        }
        return  ResponseEntity.ok(responseDto);
    }
}
