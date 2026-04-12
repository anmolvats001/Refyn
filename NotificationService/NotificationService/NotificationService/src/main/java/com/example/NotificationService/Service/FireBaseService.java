package com.example.NotificationService.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FireBaseService {
    public String sendNotification(String token, String title, String body) {
        if (token == null || token.isBlank()) {
            log.warn("Device token is null or empty, skipping notification");
            return null;
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            return FirebaseMessaging.getInstance().send(message);

        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Error sending notification", e);
        }
    }
}
