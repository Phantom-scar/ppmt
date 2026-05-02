package io.agileintelligence.ppmt.web;

import io.agileintelligence.ppmt.domain.AppUser;
import io.agileintelligence.ppmt.service.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        AppUser user = appUserService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(java.util.Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", user.getRole()
        ));
    }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public ResponseEntity<?> getAllUsers() {
        var list = appUserService.findAllUsers().stream()
            .map(user -> java.util.Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", user.getRole()
            ))
            .toList();
        return ResponseEntity.ok(list);
        }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable String id, @RequestBody io.agileintelligence.ppmt.web.dto.RoleUpdateRequest req) {
        io.agileintelligence.ppmt.domain.Role newRole;
        try {
            newRole = io.agileintelligence.ppmt.domain.Role.valueOf(req.getRole());
        } catch (Exception e) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid role");
        }

        io.agileintelligence.ppmt.domain.AppUser updated = appUserService.setUserRole(id, newRole);
        return ResponseEntity.ok(java.util.Map.of(
            "id", updated.getId(),
            "username", updated.getUsername(),
            "fullName", updated.getFullName(),
            "role", updated.getRole()
        ));
    }
}