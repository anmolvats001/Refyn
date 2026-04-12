package com.example.ApplicationsService.Response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinalResponse {
    private Boolean work;
    private String message;
}
