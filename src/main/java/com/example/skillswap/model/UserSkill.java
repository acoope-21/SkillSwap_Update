package com.example.skillswap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "user_skill")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long skillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "skill_level")
    private String skillLevel;

    @Column(name = "offering")
    private Boolean offering = false;

    @Column(name = "seeking")
    private Boolean seeking = false;

    // Constructors
    public UserSkill() {}

    public UserSkill(User user, String skillName, String skillLevel, Boolean offering, Boolean seeking) {
        this.user = user;
        this.skillName = skillName;
        this.skillLevel = skillLevel;
        this.offering = offering;
        this.seeking = seeking;
    }

    // Getters and Setters
    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

    public Boolean getOffering() { return offering; }
    public void setOffering(Boolean offering) { this.offering = offering; }

    public Boolean getSeeking() { return seeking; }
    public void setSeeking(Boolean seeking) { this.seeking = seeking; }
}
