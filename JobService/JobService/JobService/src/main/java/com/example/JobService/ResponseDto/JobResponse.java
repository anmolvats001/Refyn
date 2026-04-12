package com.example.JobService.ResponseDto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobResponse {
    private String jobId;
    private boolean work;
    private String message;
}
