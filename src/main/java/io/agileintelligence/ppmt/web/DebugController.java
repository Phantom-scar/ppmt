package io.agileintelligence.ppmt.web;

import io.agileintelligence.ppmt.domain.AppUser;
import io.agileintelligence.ppmt.domain.Role;
import io.agileintelligence.ppmt.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/set-role")
    public ResponseEntity<?> setRole(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String role = body.get("role");
        if (username == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "username and role required"));
        }

        AppUser user = appUserService.findByUsername(username)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        Role newRole;
        try {
            newRole = Role.valueOf(role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid role"));
        }

        AppUser updated = appUserService.setUserRole(user.getId(), newRole);
        return ResponseEntity.ok(Map.of("id", updated.getId(), "username", updated.getUsername(), "role", updated.getRole()));
    }
}
