package com.retailbilling.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity 
@Table(name = "roles") 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Role {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, unique = true) 
    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(
        name = "role_permissions", 
        joinColumns = @JoinColumn(name = "role_id"), 
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    ) 
    private Set<Permission> permissions = new HashSet<>();

    @Column(nullable = false, updatable = false) 
    private LocalDateTime createdAt;

    @Column(nullable = false) 
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE") 
    private Boolean deleted = false;

    @PrePersist 
    protected void onCreate() { 
        createdAt = LocalDateTime.now(); 
        updatedAt = LocalDateTime.now(); 
    }

    @PreUpdate 
    protected void onUpdate() { 
        updatedAt = LocalDateTime.now(); 
    }

    // --- MANUAL GETTER TO FIX STS/LOMBOK BLINDNESS ---
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}