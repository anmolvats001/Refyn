package com.example.UserService.Kafka;

import com.example.UserService.Entity.UserEntity;
import com.example.UserService.Repo.UserRepo;
import com.example.UserService.UserDto.DataDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Consumer {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepo userRepo;
    @KafkaListener(topics = "user-topic" , groupId = "user-group")
    public void listen(String x) throws Exception{
        DataDto dto = objectMapper.readValue(x, DataDto.class);
        System.out.println("Consumer listening.."+dto.toString());
        log.info("Consumer listening.."+dto.toString());
        userRepo.save(UserEntity.builder().userId(dto.getUserId()).username(dto.getUsername()).email(dto.getEmail()).firstName(dto.getFirstName()).lastName(dto.getLastName()).role(dto.getRole()).company(dto.getCompany()).college(dto.getCollege()).build());
        log.info("Consumer Added succesfully.");
    }
}
