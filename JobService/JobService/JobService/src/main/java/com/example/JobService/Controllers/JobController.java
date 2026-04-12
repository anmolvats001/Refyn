package com.example.JobService.Controllers;

import com.example.JobService.JobServices.JobsService;
import com.example.JobService.RequestDto.JobRequest;
import com.example.JobService.ResponseDto.FeignResponse;
import com.example.JobService.ResponseDto.JobResponse;
import com.example.JobService.ResponseDto.PageResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@RequestMapping("/api/v1/jobs")
public class JobController {
    @Autowired
    private JobsService jobsService;

    @GetMapping()
    public ResponseEntity<PageResponse> getAllJobs(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String category,
                                                   @RequestParam(required = false) String company,
                                                   @RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String location,
                                                   @RequestParam(defaultValue="0") int sort
    ) {
        log.info("Getting all jobs");
        PageResponse pageResponse = jobsService.getJobFiltered(page, size, category, company, title, location,sort);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }
    @GetMapping("/internal/single-job")  // also remove the trailing slash
    public ResponseEntity<FeignResponse> getJobById(@RequestParam String jobId) {
        FeignResponse feignResponse = jobsService.getJobById(jobId);
        return new ResponseEntity<>(feignResponse, HttpStatus.OK);
    }
    @PostMapping()
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> createJob(JwtAuthenticationToken jwtAuthenticationToken, @RequestBody JobRequest jobRequest) {
        try {
            String userId = jwtAuthenticationToken.getToken().getSubject();
            log.info("Creating new job");
            return ResponseEntity.ok(jobsService.createJob(userId, jobRequest));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PostMapping("/internal/bulk")
    public Map<String, FeignResponse> bulk(@RequestBody List<String> ids){
        return jobsService.getJobs(ids);
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> deleteJob(JwtAuthenticationToken jwtAuthenticationToken, @PathVariable String jobId) {
        try {
            String userId = jwtAuthenticationToken.getToken().getSubject();
            log.info("Deleting job");
            JobResponse jobResponse = jobsService.deleteJob(userId, jobId);
            if (jobResponse.isWork()) {
                log.info("Job has been deleted");
                return ResponseEntity.ok(jobResponse);
            } else {
                log.warn("Job can not be deleted");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> updateJob(JwtAuthenticationToken jwtAuthenticationToken, @PathVariable String jobId, @RequestBody JobRequest jobRequest) {
        try {
            String userId = jwtAuthenticationToken.getToken().getSubject();
            log.info("Updating job");
            JobResponse jobResponse = jobsService.updateJob(userId, jobId, jobRequest);
            if (jobResponse.isWork()) {
                log.info("Job has been updated");
                return ResponseEntity.ok(jobResponse);
            } else {
                log.warn("Job can not be updated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/recruiter-data")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PageResponse> getJobsPostedByYou(JwtAuthenticationToken jwtAuthenticationToken) {
        try {
            log.info("Getting jobs posted by you");
            PageResponse pageResponse = jobsService.getJobsPostedByYou(jwtAuthenticationToken);
            return new ResponseEntity<>(pageResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @PatchMapping("/change-active-status/{job-id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> changeActiveStatus(JwtAuthenticationToken jwtAuthenticationToken,@RequestBody boolean active,@PathVariable String jobId) {
        try {
            String userId = jwtAuthenticationToken.getToken().getSubject();
            log.info("Changing active status of job");
            JobResponse jobResponse = jobsService.changeActiveStatus(userId, jobId, active);
            if(jobResponse.isWork()) {
                log.info("Job has been changed");
                return ResponseEntity.ok(jobResponse);
            }
            else {
                log.warn("Job can not be changed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
