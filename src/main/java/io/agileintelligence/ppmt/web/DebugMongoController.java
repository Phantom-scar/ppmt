package io.agileintelligence.ppmt.web;

import io.agileintelligence.ppmt.domain.AppUser;
import io.agileintelligence.ppmt.domain.Role;
import io.agileintelligence.ppmt.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/mongo")
public class DebugMongoController {

    @Autowired
    private AppUserRepository appUserRepository;

    @PostMapping("/create-test-user")
    public ResponseEntity<?> createTestUser(@RequestParam String username) {
        try {
            // Check if user exists
            if (appUserRepository.existsByUsername(username)) {
                return ResponseEntity.ok(Map.of("status", "USER_EXISTS", "username", username));
            }

            // Try to save
            AppUser user = new AppUser();
            user.setUsername(username);
            user.setPassword("testpass123");
            user.setFullName("Test " + username);
            user.setRole(Role.MEMBER);

            System.out.println("[DEBUG-MONGO] Before save: " + username);
            AppUser saved = appUserRepository.save(user);
            System.out.println("[DEBUG-MONGO] After save: " + saved.getId());

            // Try to find it immediately
            var found = appUserRepository.findByUsername(username);
            System.out.println("[DEBUG-MONGO] Found after save: " + found.isPresent());

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "saved_id", saved.getId(),
                    "found_after_save", found.isPresent()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage(),
                    "type", e.getClass().getSimpleName()
            ));
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<AppUser> users = appUserRepository.findAll();
            return ResponseEntity.ok(Map.of(
                    "count", users.size(),
                    "users", users.stream()
                            .map(u -> Map.of("username", u.getUsername(), "id", u.getId()))
                            .toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/clear-all")
    public ResponseEntity<?> clearAll() {
        try {
            long count = appUserRepository.count();
            appUserRepository.deleteAll();
            return ResponseEntity.ok(Map.of("deleted_count", count));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
