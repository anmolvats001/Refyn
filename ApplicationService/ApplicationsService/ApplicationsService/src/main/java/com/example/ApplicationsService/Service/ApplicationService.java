package com.example.ApplicationsService.Service;

import com.example.ApplicationsService.Config.JobClient;
import com.example.ApplicationsService.Config.UserClient;
import com.example.ApplicationsService.Entity.ApplicationEntity;
import com.example.ApplicationsService.Event.KafkaEvent;
import com.example.ApplicationsService.Repo.ApplicationRepo;
import com.example.ApplicationsService.Request.ApplicationRequest;
import com.example.ApplicationsService.Request.FeignJobRequest;
import com.example.ApplicationsService.Response.ApplicationResponse;
import com.example.ApplicationsService.Response.FinalResponse;
import com.example.ApplicationsService.Response.PageResponse;
import com.example.ApplicationsService.Response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepo applicationRepo;
    @Autowired
    private UserClient userClient;
    @Autowired
    private JobClient jobClient;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;

    public PageResponse getApplicationsOfJobs(String jobId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("priority").descending());

        Page<ApplicationEntity> pageData =
                applicationRepo.findByJobId(jobId, pageable);

        List<String> userIds = pageData.getContent()
                .stream()
                .map(ApplicationEntity::getUserId)
                .distinct()
                .toList();

        Map<String, UserResponse> userResponses = userClient.bulk(userIds);
        FeignJobRequest feignJobRequest = jobClient.getJob(jobId);
        List<ApplicationResponse> list = pageData.getContent().stream().map(app -> {

            UserResponse user = userResponses.get(app.getUserId());

            return ApplicationResponse.builder()
                    .applicationId(app.getApplicationId())
                    .username(user != null ? user.getUsername() : null)
                    .email(user != null ? user.getEmail() : null)
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .profilePicture(user != null ? user.getProfilePicture() : null)
                    .jobId(app.getJobId())
                    .status(app.getStatus())
                    .referred(app.isReferred())
                    .refreerId(app.getRefreerId())
                    .priority(app.getPriority())
                    .skills(app.getSkills())
                    .resumeUrl(app.getResumeUrl())
                    .company(feignJobRequest.getCompany())
                    .title(feignJobRequest.getTitle())
                    .description(feignJobRequest.getDescription())
                    .build();

        }).toList();

        return PageResponse.builder()
                .applications(list)
                .currentPage(pageData.getNumber())
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .build();
    }

    public PageResponse getApplicationsOfStudent(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,Sort.by("priority").descending());

        Page<ApplicationEntity> pageData =
                applicationRepo.findByUserId(userId, pageable);

        List<String> userIds = pageData.getContent()
                .stream()
                .map(ApplicationEntity::getUserId)
                .distinct()
                .toList();

        Map<String, UserResponse> userResponses = userClient.bulk(userIds);

        List<ApplicationResponse> list = pageData.getContent().stream().map(app -> {

            UserResponse user = userResponses.get(app.getUserId());
            FeignJobRequest feignJobRequest = jobClient.getJob(app.getJobId());

            return ApplicationResponse.builder()
                    .applicationId(app.getApplicationId())
                    .username(user != null ? user.getUsername() : null)
                    .email(user != null ? user.getEmail() : null)
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .profilePicture(user != null ? user.getProfilePicture() : null)
                    .jobId(app.getJobId())
                    .status(app.getStatus())
                    .referred(app.isReferred())
                    .refreerId(app.getRefreerId())
                    .priority(app.getPriority())
                    .skills(app.getSkills())
                    .resumeUrl(app.getResumeUrl())
                    .company(feignJobRequest.getCompany())
                    .title(feignJobRequest.getTitle())
                    .description(feignJobRequest.getDescription())

                    .build();

        }).toList();

        return PageResponse.builder()
                .applications(list)
                .currentPage(pageData.getNumber())
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .build();
    }
    public FinalResponse updateByStudent(ApplicationRequest applicationRequest,String applicationId,String userId) {
        try{
            ApplicationEntity applicationEntity=applicationRepo.findById(applicationId).orElse(null);
            if(applicationEntity==null){
                return FinalResponse.builder().work(false).message("Can't get Application Data").build();
            }
            if(!applicationEntity.getUserId().equals(userId)){
                return FinalResponse.builder().work(false).message("You are not authorized to change the application Data").build();
            }
            log.info("getting Application Data");
            UserResponse userResponse=userClient.getUser(userId);
            log.info("Got User Data {}",userResponse.getUsername());
            applicationEntity.setSkills(applicationRequest.getSkills().size()!=0?applicationRequest.getSkills():applicationEntity.getSkills());
            applicationEntity.setResumeUrl(applicationRequest.getResumeUrl()!=null?applicationRequest.getResumeUrl():applicationEntity.getResumeUrl());
            applicationRepo.save(applicationEntity);
            kafkaTemplate.send("application-service",objectMapper.writeValueAsString( KafkaEvent.builder().applicationId(applicationId).jobId(applicationEntity.getJobId()).title("APPLICATION-UPDATED").deviceToken(userResponse.getDeviceToken()).userId(userId).build()));
            return FinalResponse.builder().work(true).message("Application Data is updated").build();
        }
        catch (Exception e){
            throw  new RuntimeException("Can not update Application Data",e);
        }
    }
    public FinalResponse deleteByStudent(String applicationId,String userId) {
        try{
            ApplicationEntity applicationEntity=applicationRepo.findById(applicationId).orElse(null);
            if(applicationEntity==null){
                return FinalResponse.builder().work(false).message("Can't get Application Data").build();
            }
            if(!applicationEntity.getUserId().equals(userId)){
                return FinalResponse.builder().work(false).message("You are not authorized to change the application Data").build();
            }
            applicationRepo.deleteById(applicationId);
            UserResponse userResponse=userClient.getUser(userId);
            kafkaTemplate.send("application-service",objectMapper.writeValueAsString( KafkaEvent.builder().applicationId(applicationId).jobId(applicationEntity.getJobId()).title("APPLICATION-DELETED").deviceToken(userResponse.getDeviceToken()).userId(userId).build()));
            return  FinalResponse.builder().work(true).message("Application Data is deleted").build();
        }
        catch (Exception e){
            throw  new RuntimeException("Can not delete Application Data",e);
        }
    }
    public ApplicationResponse createApplication(String userId,String jobId,ApplicationRequest applicationRequest){
        try {
            if(userId==null ||jobId==null){
                throw new RuntimeException("UserId/jobId cannot be null");
            }
            log.info("Sendong request to client");
            FeignJobRequest feignJobRequest = jobClient.getJob(jobId);
            log.info("Got data from client :{} {}",feignJobRequest.getTitle(),feignJobRequest.isActive());
            if(!feignJobRequest.isActive()){
                return null;
            }
            ApplicationEntity applicationEntity2= applicationRepo.findByUserIdAndJobId(userId,jobId);
            if(applicationEntity2!=null){
                throw new RuntimeException("Application already exists");
            }
            UserResponse userResponse = userClient.getUser(userId);
            ApplicationEntity applicationEntity= ApplicationEntity.builder().jobId(jobId).userId(userId).resumeUrl(applicationRequest.getResumeUrl()).skills(applicationRequest.getSkills()).applicationId(UUID.randomUUID().toString()).build();
            ApplicationEntity applicationEntity1=applicationRepo.save(applicationEntity);
            if(applicationEntity1!=null){
                return ApplicationResponse.builder().applicationId(applicationEntity1.getApplicationId()).resumeUrl(applicationEntity1.getResumeUrl())
                        .jobId(applicationEntity1.getJobId()).status(applicationEntity1.getStatus()).refreerId(applicationEntity1.getRefreerId()).skills(applicationEntity1.getSkills()).referred(applicationEntity1.isReferred()).priority(applicationEntity1.getPriority()).userId(applicationEntity1.getUserId()).company(feignJobRequest.getCompany())
                        .title(feignJobRequest.getTitle())
                        .description(feignJobRequest.getDescription())
                        .username(userResponse.getUsername())
                        .email(userResponse.getEmail())
                        .lastName(userResponse.getLastName())
                        .firstName(userResponse.getFirstName())
                        .profilePicture(userResponse.getProfilePicture())

                        .build();
            }
            return null;
        }
        catch (Exception e){
            throw  new RuntimeException("Can not create Application Data",e);
        }
    }
    public PageResponse getApplicationByRefreerId(String userId,int page,int size) {
        Pageable pageable = PageRequest.of(page, size,Sort.by("priority").descending());

        Page<ApplicationEntity> pageData =
                applicationRepo.findByRefreerId(userId, pageable);

        List<String> userIds = pageData.getContent()
                .stream()
                .map(ApplicationEntity::getUserId)
                .distinct()
                .toList();

        Map<String, UserResponse> userResponses = userClient.bulk(userIds);
        List<ApplicationResponse> list = pageData.getContent().stream().map(app -> {

            UserResponse user = userResponses.get(app.getUserId());
            FeignJobRequest feignJobRequest = jobClient.getJob(app.getJobId());

            return ApplicationResponse.builder()
                    .applicationId(app.getApplicationId())
                    .username(user != null ? user.getUsername() : null)
                    .email(user != null ? user.getEmail() : null)
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .profilePicture(user != null ? user.getProfilePicture() : null)
                    .jobId(app.getJobId())
                    .status(app.getStatus())
                    .referred(app.isReferred())
                    .refreerId(app.getRefreerId())
                    .priority(app.getPriority())
                    .skills(app.getSkills())
                    .resumeUrl(app.getResumeUrl())
                    .company(feignJobRequest.getCompany())
                    .title(feignJobRequest.getTitle())
                    .description(feignJobRequest.getDescription())

                    .build();

        }).toList();

        return PageResponse.builder()
                .applications(list)
                .currentPage(pageData.getNumber())
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .build();

    }
    public FinalResponse changeStatus(String recruiterId,String applicationId,String status){
        try {
            if(recruiterId==null||applicationId==null||status==null){
                throw new RuntimeException("recruiterId/applicationId/status cannot be null");
            }
            ApplicationEntity applicationEntity= applicationRepo.findById(applicationId).orElseThrow(()->new RuntimeException("Application Id not found"));
            if(applicationEntity.getStatus().equals("HIRED")||applicationEntity.getStatus().equals("REJECTED"))return FinalResponse.builder().work(false).message("Application Status can not be changed").build();
            applicationEntity.setStatus(status.toUpperCase());
            applicationRepo.save(applicationEntity);
            UserResponse userResponse=userClient.getUser(recruiterId);
            kafkaTemplate.send("application-service",objectMapper.writeValueAsString( KafkaEvent.builder().applicationId(applicationId).jobId(applicationEntity.getJobId()).title("APPLICATION-STATUS-CHANGED").deviceToken(userResponse.getDeviceToken()).userId(recruiterId).build()));

            return FinalResponse.builder().work(true).message("Application Status Changed Successfully").build();
        }
        catch (Exception e){
            throw  new RuntimeException("Can not change Status",e);
        }
    }
    public FinalResponse changePriority(String employeeId,String applicationId){
        try {
            if(employeeId==null||applicationId==null){
                throw new RuntimeException("employeeId/applicationId cannot be null");
            }
            log.info("Getting user data");
            UserResponse userResponse=userClient.getUser(employeeId);
            log.info("Got user data:{}",userResponse.getRole());
            if(userResponse==null){
                throw new RuntimeException("employeeId/applicationId cannot be null");
            }
            if(!userResponse.getRole().toUpperCase().equals("EMPLOYEE")){
                throw new AuthenticationException("You are not authorized to perform this action") {};
            }
            log.info("Getting application data");
            log.info("Getting application data:{}",applicationId);
            ApplicationEntity applicationEntity=applicationRepo.findById(applicationId).orElseThrow(()->new RuntimeException("Application Id not found"));
            log.info("Got application Id:{}",applicationEntity.getApplicationId());
            if(applicationEntity.getRefreerId().contains(employeeId)){
                return FinalResponse.builder().work(false).message("You already referred").build();
            }
            FeignJobRequest feignJobRequest = jobClient.getJob(applicationEntity.getJobId());
            log.info("Got feignJobRequest:{}",feignJobRequest.getTitle());
            if(!feignJobRequest.isActive()){
                return FinalResponse.builder().work(false).message("Job is not active").build();
            }
            applicationEntity.setPriority(applicationEntity.getPriority()+1);
            List<String> refreerIds = applicationEntity.getRefreerId();
            refreerIds.add(userResponse.getUserId());
            applicationEntity.setRefreerId(refreerIds);
            applicationEntity.setReferred(true);
            applicationRepo.save(applicationEntity);
            log.info("Application Status Changed Successfully");
            kafkaTemplate.send("application-service",objectMapper.writeValueAsString( KafkaEvent.builder().applicationId(applicationId).jobId(applicationEntity.getJobId()).title("APPLICATION-REFERRED").deviceToken(userResponse.getDeviceToken()).userId(employeeId).build()));

            return FinalResponse.builder().work(true).message("Application Priority is increased Successfully").build();
        }
        catch (Exception e){
            throw  new RuntimeException("Can not change Priority",e);
        }
    }
    @KafkaListener(topics = "delete-all-applications", groupId = "application-service")
    public void deleteApplicationsOfUser(String userId) {
        try {

            applicationRepo.deleteAllByUserId(userId);

            log.info("Deleted all applications for user: {}", userId);
        }
        catch (Exception e){
            log.error("Can not delete applications for user {}",userId,e);
        }
    }
}