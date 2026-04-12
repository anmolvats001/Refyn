package com.example.UserService.Kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

@Slf4j
public class DeleteProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    public void deleteUserFromKeycloak( String value){
        log.info("Sending data ");
        kafkaTemplate.send("delete-user", value);
    }
    public void deleteAllUserApplications(String userId){
        log.info("Sending data ");
        kafkaTemplate.send("delete-all-applications", userId);
    }
}
