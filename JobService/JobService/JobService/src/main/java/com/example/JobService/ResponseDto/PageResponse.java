package com.example.JobService.ResponseDto;

import com.example.JobService.Entity.JobEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class PageResponse {
    List<JobEntity> jobs;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
