package io.agileintelligence.ppmt.service;

import io.agileintelligence.ppmt.domain.AppUser;
import io.agileintelligence.ppmt.domain.Role;
import io.agileintelligence.ppmt.repository.AppUserRepository;
import io.agileintelligence.ppmt.web.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AppUser registerUser(SignupRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole(request.getRole() == null ? Role.MEMBER : request.getRole());

        System.out.println("[DEBUG] Registering user: " + user.getUsername());
        AppUser saved = appUserRepository.save(user);
        System.out.println("[DEBUG] User saved with ID: " + saved.getId());
        return saved;
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public List<AppUser> findAllUsers() {
        return appUserRepository.findAll();
    }

    public AppUser getCurrentUser(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found"));
    }

    public AppUser setUserRole(String userId, io.agileintelligence.ppmt.domain.Role role) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setRole(role);
        return appUserRepository.save(user);
    }
}