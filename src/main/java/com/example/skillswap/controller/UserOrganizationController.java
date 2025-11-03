package com.example.skillswap.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.skillswap.model.UserOrganization;
import com.example.skillswap.repository.UserOrganizationRepository;

@RestController
@RequestMapping("/api/organizations")
public class UserOrganizationController {

    private final UserOrganizationRepository repo;

    public UserOrganizationController(UserOrganizationRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<UserOrganization> getAllOrganizations() {
        return repo.findAll();
    }

    @PostMapping
    public UserOrganization addOrganization(@RequestBody UserOrganization organization) {
        return repo.save(organization);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
