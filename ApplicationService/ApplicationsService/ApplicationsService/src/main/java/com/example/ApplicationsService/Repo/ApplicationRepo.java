package com.example.ApplicationsService.Repo;

import com.example.ApplicationsService.Entity.ApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepo  extends JpaRepository<ApplicationEntity,String> {
    Page<ApplicationEntity> findByUserId(String userId, Pageable pageable);
    Page<ApplicationEntity> findByJobId(String jobId, Pageable pageable);
    Page<ApplicationEntity> findByRefreerId(String refreerId,Pageable pageable);
    ApplicationEntity findByUserIdAndJobId(String userId,String jobId);
    void deleteAllByUserId(String userId);
}
