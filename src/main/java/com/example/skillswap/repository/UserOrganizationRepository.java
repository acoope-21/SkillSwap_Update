package com.example.skillswap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.skillswap.model.UserOrganization;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {
}
