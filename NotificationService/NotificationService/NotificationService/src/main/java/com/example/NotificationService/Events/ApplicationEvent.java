package com.example.NotificationService.Events;


import lombok.*;
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEvent {
    String applicationId;
    String jobId;
    String title;
    String deviceToken;
    String userId;
}