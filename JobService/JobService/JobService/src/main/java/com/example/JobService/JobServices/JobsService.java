package com.example.JobService.JobServices;

import com.example.JobService.Entity.JobEntity;
import com.example.JobService.Repo.JobRepo;
import com.example.JobService.RequestDto.JobRequest;
import com.example.JobService.ResponseDto.FeignResponse;
import com.example.JobService.ResponseDto.JobResponse;
import com.example.JobService.ResponseDto.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobsService {
    @Autowired
    private JobRepo jobRepo;
    public PageResponse getJobFiltered(int page, int size, String category, String company, String title, String location, int sort) {
        size = Math.min(size, 10);
        Pageable pageable;
        if (sort == 1) {
            pageable = PageRequest.of(page, size, Sort.by("salary").descending());
        } else if (sort == 2) {
            pageable = PageRequest.of(page, size, Sort.by("salary").ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        }
        Page<JobEntity> jobPage;

        if (category != null && company != null && location != null) {
            jobPage = jobRepo.findByCategoryIgnoreCaseAndLocationIgnoreCaseAndCompanyIgnoreCase(company, location, category, pageable);
        }
        else if (category != null && location != null) {
            jobPage = jobRepo.findByCategoryIgnoreCaseAndLocationIgnoreCase(category, location, pageable);
        } else if (category != null && company != null) {
            jobPage = jobRepo.findByCategoryIgnoreCaseAndCompanyIgnoreCase(category, company, pageable);
        } else if (title != null) {
            jobPage = jobRepo.findByTitleContainingIgnoreCase(title, pageable);
        } else if (company != null) {
            jobPage = jobRepo.findByCompanyContainingIgnoreCase(company, pageable);
        } else if (category != null) {
            jobPage = jobRepo.findByCategoryIgnoreCase(category, pageable);
        } else if (location != null) {
            jobPage = jobRepo.findByLocationContainingIgnoreCase(location, pageable);
        } else {
            jobPage = jobRepo.findAll(pageable);
        }

        return PageResponse.builder()
                .jobs(jobPage.getContent())
                .currentPage(jobPage.getNumber())
                .totalPages(jobPage.getTotalPages())
                .totalElements(jobPage.getTotalElements())
                .build();
    }

    public JobResponse createJob(String userId, JobRequest jobRequest) {
        if (jobRequest == null) {
            throw new RuntimeException("Some Data is missing");
        }
        if (userId == null) {
            throw new RuntimeException("User is not authorized");
        }
        JobEntity jobEntity = jobRepo.save(JobEntity.builder().jobId(UUID.randomUUID().toString()).recruiterId(userId).title(jobRequest.getTitle()).description(jobRequest.getDescription()).company(jobRequest.getCompany()).location(jobRequest.getLocation()).skills(jobRequest.getSkills()).salary(jobRequest.getSalary()).category(jobRequest.getCategory()).build());
        return JobResponse.builder().jobId(jobEntity.getJobId()).work(true).message("Job has been created successfully").build();
    }

    public JobResponse deleteJob(String userId, String jobId) {
        if (jobId == null || userId == null) {
            throw new RuntimeException("Some Data is missing");
        }
        Optional<JobEntity> jobEntity = jobRepo.findById(jobId);
        if (jobEntity.isPresent() && jobEntity.get().getRecruiterId().equals(userId)) {
            jobRepo.deleteById(jobId);
            return JobResponse.builder().jobId(jobId).work(true).message("Job has been deleted successfully").build();
        } else {
            return JobResponse.builder().jobId(jobId).message("Can not delete").work(false).build();
        }
    }
    public JobResponse updateJob(String userId, String jobId, JobRequest jobRequest) {
        if (jobRequest == null) {
            return JobResponse.builder().jobId(jobId).work(true).message("Job has been updated successfully").build();
        }
        if (jobId == null || userId == null) {
            throw new RuntimeException("Some Data is missing");
        }
        Optional<JobEntity> jobEntity = jobRepo.findById(jobId);
        if (jobEntity.isPresent() && jobEntity.get().getRecruiterId().equals(userId)) {
            JobEntity jobEntity1 = jobEntity.get();
            jobEntity1.setTitle(jobRequest.getTitle());
            jobEntity1.setDescription(jobRequest.getDescription());
            jobEntity1.setSalary(jobRequest.getSalary());
            jobEntity1.setLocation(jobRequest.getLocation());
            jobEntity1.setCategory(jobRequest.getCategory());
            jobEntity1.setSkills(jobRequest.getSkills());

            jobRepo.save(jobEntity1);
            return JobResponse.builder().jobId(jobId).work(true).message("Nothing to change").build();
        }
        return JobResponse.builder().jobId(jobId).work(false).message("Can not update").build();
    }

    public PageResponse getJobsPostedByYou(JwtAuthenticationToken jwtAuthenticationToken) {
        String userId = jwtAuthenticationToken.getToken().getSubject();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<JobEntity> jobPage = jobRepo.findByRecruiterId(userId, pageRequest);
        return PageResponse.builder()
                .jobs(jobPage.getContent())
                .currentPage(jobPage.getNumber())
                .totalPages(jobPage.getTotalPages())
                .totalElements(jobPage.getTotalElements())
                .build();
    }

    public FeignResponse getJobById(String id) {
        log.info("Getting data");
        JobEntity jobEntity = jobRepo.findById(id).orElseThrow(()-> new RuntimeException("job not found"));
        if (jobEntity!=null) {
            return FeignResponse.builder().company(jobEntity.getCompany()).category(jobEntity.getCategory()).title(jobEntity.getTitle()).description(jobEntity.getDescription()).active(jobEntity.isActive()).build();
        } else {
            log.error("job not found");
            throw new RuntimeException("Can not find job with id " + id);
        }
    }

    public Map<String, FeignResponse> getJobs(List<String> ids) {
        log.info("Getting All Users with ids");
        List<JobEntity> users = jobRepo.findByJobIdIn(ids);
        return users.stream()
                .collect(Collectors.toMap(
                        JobEntity::getJobId,
                        user -> FeignResponse.builder()
                                .company(user.getCompany())
                                .category(user.getCategory())
                                .description(user.getDescription())
                                .title(user.getTitle())
                                .active(user.isActive())
                                .build()
                ));
    }

    public JobResponse changeActiveStatus(String userId, String jobId, boolean active) {
        if(jobId==null || userId==null || active==false) {
            return JobResponse.builder().work(false).message("Credential missing").jobId(jobId).build();
        }
        JobEntity jobEntity = jobRepo.findById(jobId).get();
        if(jobEntity==null){
            return JobResponse.builder().work(false).message("Active status can not be changed").jobId(jobId).build();
        }
        if(jobEntity.getRecruiterId().equals(userId)) {
            jobEntity.setActive(active);
            jobRepo.save(jobEntity);
            return JobResponse.builder().work(true).message("Job has been changed successfully").jobId(jobId).build();
        }
        return JobResponse.builder().work(false).message("Active status can not be changed").jobId(jobId).build();
    }
}
