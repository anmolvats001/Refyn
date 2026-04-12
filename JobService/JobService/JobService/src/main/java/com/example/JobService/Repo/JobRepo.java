package com.example.JobService.Repo;

import com.example.JobService.Entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface JobRepo extends JpaRepository<JobEntity, String> {

    Page<JobEntity> findByCategoryIgnoreCase(String category, Pageable pageable);

    Page<JobEntity> findByCompanyContainingIgnoreCase(String company, Pageable pageable);

    Page<JobEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<JobEntity> findByCategoryIgnoreCaseAndLocationIgnoreCase(
            String category,
            String location,
            Pageable pageable
    );

    Page<JobEntity> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    Page<JobEntity> findByCategoryIgnoreCaseAndCompanyIgnoreCase(
            String category,
            String company,
            Pageable pageable
    );

    Page<JobEntity> findByCategoryIgnoreCaseAndLocationIgnoreCaseAndCompanyIgnoreCase(
            String category,
            String location,
            String company,
            Pageable pageable
    );

    Page<JobEntity> findByRecruiterId(String recruiterId, Pageable pageable);

    List<JobEntity> findByJobIdIn(List<String> ids);

}