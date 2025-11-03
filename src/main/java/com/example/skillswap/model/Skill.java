package com.example.skillswap.model;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "skill_name", nullable = false, unique = true)
    private String skillName;

    @Column(name = "skill_category")
    private String skillCategory;

    @Column(name = "description")
    private String description;

    public Skill() {}

    public Skill(String skillName, String skillCategory, String description) {
        this.skillName = skillName;
        this.skillCategory = skillCategory;
        this.description = description;
    }

    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getSkillCategory() { return skillCategory; }
    public void setSkillCategory(String skillCategory) { this.skillCategory = skillCategory; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
