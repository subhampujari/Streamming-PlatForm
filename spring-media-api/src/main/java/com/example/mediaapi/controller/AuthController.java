package com.example.mediaapi.controller;

import com.example.mediaapi.dto.AuthDtos.*;
import com.example.mediaapi.model.AdminUser;
import com.example.mediaapi.repository.AdminUserRepository;
import com.example.mediaapi.security.JwtService;
import com.example.mediaapi.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AdminUserService userService;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final AdminUserRepository userRepo;

    public AuthController(AdminUserService userService, AuthenticationManager authManager, JwtService jwtService, AdminUserRepository userRepo) {
        this.userService = userService;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        AdminUser created = userService.signup(req.email(), req.password());
        return ResponseEntity.ok(Map.of("id", created.getId(), "email", created.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(req.email(), Map.of("role", "ADMIN"));
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
