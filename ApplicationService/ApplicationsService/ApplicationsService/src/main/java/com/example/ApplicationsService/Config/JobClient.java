package com.example.ApplicationsService.Config;

import com.example.ApplicationsService.Request.FeignJobRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "job-service",url = "http://jobservice:8088")
public interface JobClient {
    @GetMapping("/api/v1/jobs/internal/single-job")
     FeignJobRequest getJob(@RequestParam String jobId);
    @PostMapping("/api/v1/jobs/internal/bulk")
    Map<String,FeignJobRequest> bulk(@RequestBody List<String> jobId);
}
