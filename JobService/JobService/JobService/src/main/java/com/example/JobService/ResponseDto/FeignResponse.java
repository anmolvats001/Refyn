package com.example.JobService.ResponseDto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeignResponse {
    String title;
    String description;
    String category;
    String company;
    boolean active;
}
