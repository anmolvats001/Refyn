package com.example.AuthService.KafkaConsumer;
import com.example.AuthService.Service.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {
    @Autowired
    KeycloakService keycloakService;
    @KafkaListener(topics = "delete-user",groupId = "auth-group")
    public void consume(String message){
        log.info("Received Message: " + message);
        keycloakService.deleteUser(message);
        log.info("Keycloak User Deleted Successfully");
    }
}
