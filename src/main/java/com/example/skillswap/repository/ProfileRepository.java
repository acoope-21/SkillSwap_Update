package com.example.skillswap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.skillswap.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
