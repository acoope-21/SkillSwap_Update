package com.example.skillswap.controller;

import com.example.skillswap.model.UserInterest;
import com.example.skillswap.repository.UserInterestRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/interests")
public class UserInterestController {

    private final UserInterestRepository repo;

    public UserInterestController(UserInterestRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<UserInterest> getAllInterests() {
        return repo.findAll();
    }

    @GetMapping("/{userId}")
    public List<UserInterest> getUserInterests(@PathVariable Long userId) {
        return repo.findByUser_UserId(userId);
    }

    @PostMapping
    public UserInterest addInterest(@RequestBody UserInterest interest) {
        return repo.save(interest);
    }

    @DeleteMapping("/{id}")
    public void deleteInterest(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
