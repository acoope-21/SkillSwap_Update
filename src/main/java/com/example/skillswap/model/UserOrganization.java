package com.example.skillswap.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "user_organization")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long orgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "role")
    private String role;

    // Constructors
    public UserOrganization() {}

    // Getters and Setters
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
