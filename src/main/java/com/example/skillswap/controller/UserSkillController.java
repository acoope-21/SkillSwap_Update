package com.example.skillswap.controller;

import com.example.skillswap.model.UserSkill;
import com.example.skillswap.repository.UserSkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-skills")
public class UserSkillController {

    private final UserSkillRepository repo;

    public UserSkillController(UserSkillRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<UserSkill> getAllUserSkills() {
        return repo.findAll();
    }

    @GetMapping("/{userId}")
    public List<UserSkill> getUserSkills(@PathVariable Long userId) {
        return repo.findByUserUserId(userId);
    }

    @PostMapping
    public UserSkill addUserSkill(@RequestBody UserSkill userSkill) {
        return repo.save(userSkill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSkill(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
