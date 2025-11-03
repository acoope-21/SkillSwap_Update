package com.example.skillswap.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.skillswap.model.Profile;
import com.example.skillswap.repository.ProfileRepository;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileRepository repo;

    public ProfileController(ProfileRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Profile> getAllProfiles() {
        return repo.findAll();
    }

    @PostMapping
    public Profile addProfile(@RequestBody Profile profile) {
        return repo.save(profile);
    }
}
