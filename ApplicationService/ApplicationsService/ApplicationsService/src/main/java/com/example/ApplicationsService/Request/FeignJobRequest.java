package com.example.ApplicationsService.Request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeignJobRequest {
    String title;
    String description;
    String category;
    String company;
    boolean active;
}
