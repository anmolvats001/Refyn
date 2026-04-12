package com.example.UserService.Repo;

import com.example.UserService.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<UserEntity,String> {
    List<UserEntity> findByUserIdIn(List<String> ids);

}
