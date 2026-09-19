package com.retailbilling.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity 
@Table(name = "users") 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class User {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, unique = true) 
    private String username;

    @Column(nullable = false, unique = true) 
    private String email;

    @Column(nullable = false) 
    private String passwordHash;

    private String firstName;
    private String lastName;
    private String phone;

    // --- OTP FIELDS FOR FORGOT PASSWORD ---
    private String otp;
    private LocalDateTime otpExpiry;

    @Enumerated(EnumType.STRING) 
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(
        name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id")
    ) 
    private Set<Role> roles = new HashSet<>();

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

    // --- MANUAL SETTERS ---
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    // --- MANUAL GETTERS ---
    public String getPasswordHash() {
        return this.passwordHash;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public String getOtp() {
        return this.otp;
    }

    public LocalDateTime getOtpExpiry() {
        return this.otpExpiry;
    }
    
    public enum UserStatus { ACTIVE, INACTIVE, SUSPENDED }
}