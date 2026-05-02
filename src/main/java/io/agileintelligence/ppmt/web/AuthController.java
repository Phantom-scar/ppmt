package io.agileintelligence.ppmt.web;

import io.agileintelligence.ppmt.domain.AppUser;
import io.agileintelligence.ppmt.security.CustomUserDetails;
import io.agileintelligence.ppmt.security.JwtService;
import io.agileintelligence.ppmt.service.AppUserService;
import io.agileintelligence.ppmt.web.dto.AuthResponse;
import io.agileintelligence.ppmt.web.dto.LoginRequest;
import io.agileintelligence.ppmt.web.dto.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        System.out.println("[DEBUG] Signup request for: " + request.getUsername());
        AppUser user = appUserService.registerUser(request);
        UserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        System.out.println("[DEBUG] Signup completed for user: " + user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getUsername(), user.getFullName(), user.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        AppUser user = principal.getUser();

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getFullName(), user.getRole()));
    }
}