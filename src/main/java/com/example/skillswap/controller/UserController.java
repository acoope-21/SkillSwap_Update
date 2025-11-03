package com.example.skillswap.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.skillswap.model.User;
import com.example.skillswap.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        return repo.save(user);
    }
}
