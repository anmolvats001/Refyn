package com.example.UserService.Service;

import com.example.UserService.Entity.UserEntity;
import com.example.UserService.Kafka.DeleteProducer;
import com.example.UserService.Repo.UserRepo;
import com.example.UserService.UserDto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    DeleteProducer deleteProducer;

    public UserEntity getProfile(String userId) {
        try {
            UserEntity user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            log.info("getProfile: userId={}", userId);
            return user;
        } catch (Exception e) {
            log.error("Error occured in findind User data");
            throw new RuntimeException(e.getMessage());
        }
    }
    public UserResponse getProfileForFeign(String userId) {
        try {
            UserEntity user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            log.info("getProfile: userId={}", userId);
            return UserResponse.builder().role(user.getRole()).userId(user.getUserId()).profilePicture(user.getProfilePicture()).email(user.getEmail()).username(user.getUsername())
                    .firstName(user.getFirstName()).lastName(user.getLastName()).deviceToken(user.getDeviceToken()).build();
        } catch (Exception e) {
            log.error("Error occured in findind User data");
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean deleteStudentProfile(String userId) {
        try {
            Optional<UserEntity> userEntity = userRepo.findById(userId);
            if (userEntity.isPresent()) {
                userRepo.deleteById(userId);
                deleteProducer.deleteUserFromKeycloak(userId);
                deleteProducer.deleteAllUserApplications(userId);
                log.info("deleteStudentProfile: userId={}", userId);
                return true;
            } else {
                log.warn(" can not deleteStudentProfile: userId={}", userId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error occured in findind/DELETE  User data");
            throw new RuntimeException(e.getMessage());
        }
    }

    public UserEntity updateStudentProfile(String userId, UserEntity user) {
        try {
            Optional<UserEntity> userEntity = userRepo.findById(userId);
            if (userEntity.isPresent()) {
                log.info("updateStudentProfile: userId={}", userId);
                user.setUserId(userId);
                return userRepo.save(user);
            } else {
                log.warn("can not updateStudentProfile: userId={}", userId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error occured in findind/UPDATE  User data");
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<UserEntity> getAllStudents() {
        List<UserEntity> list = userRepo.findAll();
        if (list.isEmpty()) {
            log.warn("No user is present");
            throw new RuntimeException("No user is present");
        }
        log.info("getAllStudents, first UserId: userId={}", list.get(0).getUserId());
        return list;
    }

    public String getByToken(JwtAuthenticationToken token) {
        try {
            String userId = token.getToken().getSubject();
            if (userRepo.findById(userId).isPresent()) {
                log.info("getByToken: userId={}", userId);
                return userId;
            }
            log.warn("User not found");
            return null;
        } catch (Exception e) {
            log.error("Error occured in findind User data");
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, UserResponse> getUsers(List<String> ids) {
        log.info("Getting All Users with ids");
        List<UserEntity> users = userRepo.findByUserIdIn(ids);
        return users.stream()
                .collect(Collectors.toMap(
                        UserEntity::getUserId,
                        user -> UserResponse.builder()
                                .userId(user.getUserId())
                                .username(user.getUsername())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .profilePicture(user.getProfilePicture())
                                .role(user.getRole())
                                .deviceToken(user.getDeviceToken())
                                .build()
                ));
    }
    public void changeDeviceToken(String userId,String deviceToken) {
        if(deviceToken==null){
            throw new RuntimeException("deviceToken is null");
        }
        else{
            UserEntity user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            if(user!=null){
                user.setDeviceToken(deviceToken);
                userRepo.save(user);
            }
        }
    }
}