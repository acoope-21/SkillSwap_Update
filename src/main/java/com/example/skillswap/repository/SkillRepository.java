package com.example.skillswap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.skillswap.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
