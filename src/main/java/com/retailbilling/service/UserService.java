package com.retailbilling.service;

import com.retailbilling.entity.Role;
import com.retailbilling.entity.User;
import com.retailbilling.repository.RoleRepository;
import com.retailbilling.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createNewUser(
            String username,
            String email,
            String rawPassword,
            String roleName) {

        Role assignedRole = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new RuntimeException("Role not found: " + roleName));

        User newUser = new User();

        newUser.setUsername(username);
        newUser.setEmail(email);

        // Store BCrypt hash, NOT plain password
        newUser.setPasswordHash(
                passwordEncoder.encode(rawPassword)
        );

        newUser.setStatus(User.UserStatus.ACTIVE);
        newUser.setRoles(Set.of(assignedRole));

        return userRepository.save(newUser);
    }

    public User authenticate(
            String username,
            String password) {

        User user = userRepository
                .findByUsernameOrEmail(username, username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid Username or Password"));

        if (!passwordEncoder.matches(
                password,
                user.getPasswordHash())) {

            throw new RuntimeException(
                    "Invalid Username or Password");
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException(
                    "Your account is currently inactive or suspended.");
        }

        return user;
    }
}