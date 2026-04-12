package com.example.ApplicationsService.Controller;

import com.example.ApplicationsService.Event.KafkaEvent;
import com.example.ApplicationsService.Request.ApplicationRequest;
import com.example.ApplicationsService.Response.ApplicationResponse;
import com.example.ApplicationsService.Response.FinalResponse;
import com.example.ApplicationsService.Response.PageResponse;
import com.example.ApplicationsService.Service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/application")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;
    @GetMapping("/recruiter-or-employee")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('EMPLOYEE')")
    public ResponseEntity<PageResponse> getApplicationsOfJobs(JwtAuthenticationToken jwtAuthenticationToken, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam String jobId) {
        PageResponse pageResponse = applicationService.getApplicationsOfJobs(jobId, page, size);
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse> getApplicationsOfStudent(JwtAuthenticationToken jwtAuthenticationToken, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            String StudentId = jwtAuthenticationToken.getToken().getSubject();
            PageResponse pageResponse = applicationService.getApplicationsOfStudent(StudentId, page, size);
            return ResponseEntity.ok(pageResponse);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PatchMapping("/{application-id}")
    public ResponseEntity<FinalResponse> updateApplication(JwtAuthenticationToken jwtAuthenticationToken, @PathVariable("application-id") String applicationId, @RequestBody ApplicationRequest applicationRequest) {
        try {
            String StudentId = jwtAuthenticationToken.getToken().getSubject();
            FinalResponse finalResponse = applicationService.updateByStudent(applicationRequest, applicationId, StudentId);
            if (finalResponse.getWork()) {

                return ResponseEntity.ok(finalResponse);
            }
            return ResponseEntity.badRequest().body(finalResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FinalResponse(false, e.getMessage()));}
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{application-id}")
    public ResponseEntity<FinalResponse> deleteApplication(JwtAuthenticationToken jwtAuthenticationToken, @PathVariable("application-id") String applicationId) {
        try {
            String StudentId = jwtAuthenticationToken.getToken().getSubject();
            FinalResponse finalResponse = applicationService.deleteByStudent(applicationId, StudentId);
            if (finalResponse.getWork()) {
                return ResponseEntity.ok(finalResponse);
            } else {
                return ResponseEntity.badRequest().body(finalResponse);
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FinalResponse(false, e.getMessage()));}
    }

    @PostMapping("/{jobId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationResponse> createApplication(JwtAuthenticationToken jwtAuthenticationToken, @PathVariable("jobId") String jobId, @RequestBody ApplicationRequest applicationRequest) {
        try {
            String StudentId = jwtAuthenticationToken.getToken().getSubject();
            ApplicationResponse applicationResponse = applicationService.createApplication(StudentId, jobId, applicationRequest);
            if (applicationResponse != null) {
                return ResponseEntity.ok(applicationResponse);
            } else {
                ResponseEntity<ApplicationResponse> responseEntity = ResponseEntity.noContent().build();
                return responseEntity;
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body( ApplicationResponse.builder().build());}
    }

    @GetMapping("/refreer-application")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<PageResponse> getApplicationsByRefreerId(JwtAuthenticationToken jwtAuthenticationToken, @RequestParam int page, @RequestParam int size) {
        try {
            String refreerId = jwtAuthenticationToken.getToken().getSubject();
            PageResponse pageResponse = applicationService.getApplicationByRefreerId(refreerId, page, size);
            return ResponseEntity.ok(pageResponse);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @PreAuthorize("hasRole('RECRUITER')")
    @PatchMapping("/status")
    public ResponseEntity<FinalResponse> updateStatus(JwtAuthenticationToken jwtAuthenticationToken, @RequestParam String status, @RequestParam String applicationId) {
        try {
            String recruiterId = jwtAuthenticationToken.getToken().getSubject();
            FinalResponse finalResponse= applicationService.changeStatus(recruiterId,applicationId,status);
            if (finalResponse.getWork()) {
                return ResponseEntity.ok(finalResponse);
            }
            return ResponseEntity.badRequest().body(finalResponse);
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FinalResponse(false, e.getMessage()));}
    }
    @PatchMapping("/refreer-change/{applicationId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity <FinalResponse> changePriority(JwtAuthenticationToken jwtAuthenticationToken,@PathVariable String applicationId){
        try {
            String employeeId = jwtAuthenticationToken.getToken().getSubject();
            FinalResponse finalResponse= applicationService.changePriority(employeeId,applicationId);
            if (finalResponse.getWork()) {
                return ResponseEntity.ok(finalResponse);
            }
            return ResponseEntity.badRequest().body(finalResponse);
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FinalResponse(false, e.getMessage()));}
    }
}
