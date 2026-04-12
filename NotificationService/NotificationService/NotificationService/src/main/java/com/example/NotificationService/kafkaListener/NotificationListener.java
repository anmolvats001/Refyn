package com.example.NotificationService.kafkaListener;

import com.example.NotificationService.Events.ApplicationEvent;
import com.example.NotificationService.Service.FireBaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationListener {

    @Autowired
    private FireBaseService firebaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.kafka.annotation.KafkaListener(
            topics = "application-service",
            groupId = "notification-group"
    )
    public void listenApplication(String eventData) {
        try {
            ApplicationEvent event =
                    objectMapper.readValue(eventData, ApplicationEvent.class);

            log.info("Received event: {}", event);

            switch (event.getTitle()) {

                case "APPLICATION-UPDATED" ->
                        firebaseService.sendNotification(
                                event.getDeviceToken(),
                                "Application Updated",
                                "Your application was updated successfully"
                        );

                case "APPLICATION-DELETED" ->
                        firebaseService.sendNotification(
                                event.getDeviceToken(),
                                "Application Deleted",
                                "Your application was deleted"
                        );

                case "APPLICATION-REFERRED" ->
                        firebaseService.sendNotification(
                                event.getDeviceToken(),
                                "Application Referred",
                                "Your application got a referral boost 🚀"
                        );

                case "APPLICATION-STATUS-CHANGED" ->

                        firebaseService.sendNotification(
                                event.getDeviceToken(),
                                "Status Updated",
                                "Your application status has changed"
                        );

                default -> log.warn("Unknown event type: {}", event.getTitle());
            }

        } catch (Exception e) {
            log.error("Error processing Kafka event", e);
        }
    }

}