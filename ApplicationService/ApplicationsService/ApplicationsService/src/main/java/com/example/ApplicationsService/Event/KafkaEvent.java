package com.example.ApplicationsService.Event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaEvent {
    String applicationId;
    String jobId;
    String title;
    String deviceToken;
    String userId;
}