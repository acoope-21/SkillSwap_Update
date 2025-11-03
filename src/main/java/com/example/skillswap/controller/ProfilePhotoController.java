package com.example.skillswap.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.skillswap.model.ProfilePhoto;
import com.example.skillswap.repository.ProfilePhotoRepository;

@RestController
@RequestMapping("/api/photos")
public class ProfilePhotoController {

    private final ProfilePhotoRepository repo;

    public ProfilePhotoController(ProfilePhotoRepository repo) {
        this.repo = repo;
    }

    // GET all photos
    @GetMapping
    public List<ProfilePhoto> getAllPhotos() {
        return repo.findAll();
    }

    // NEW: GET all photos by profile_id
    @GetMapping("/{profileId}")
    public List<ProfilePhoto> getPhotosByProfile(@PathVariable Long profileId) {
        return repo.findByProfile_ProfileId(profileId);
    }

    // POST add a new photo
    @PostMapping
    public ProfilePhoto addPhoto(@RequestBody ProfilePhoto photo) {
        return repo.save(photo);
    }
}
