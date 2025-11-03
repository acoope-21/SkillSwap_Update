package com.example.skillswap.repository;

import com.example.skillswap.model.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUser_UserId(Long userId);
}
