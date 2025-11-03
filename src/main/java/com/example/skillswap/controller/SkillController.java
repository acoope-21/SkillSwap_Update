package com.example.skillswap.controller;

import com.example.skillswap.model.Skill;
import com.example.skillswap.repository.SkillRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillRepository repo;

    public SkillController(SkillRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Skill> getAllSkills() {
        return repo.findAll();
    }

    @PostMapping
    public Skill addSkill(@RequestBody Skill skill) {
        return repo.save(skill);
    }

    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
