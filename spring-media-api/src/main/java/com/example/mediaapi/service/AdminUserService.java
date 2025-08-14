package com.example.mediaapi.service;

import com.example.mediaapi.model.AdminUser;
import com.example.mediaapi.repository.AdminUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AdminUserService {
    private final AdminUserRepository repo;
    private final PasswordEncoder encoder;

    public AdminUserService(AdminUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public AdminUser signup(String email, String rawPassword) {
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        AdminUser user = AdminUser.builder()
                .email(email)
                .hashedPassword(encoder.encode(rawPassword))
                .createdAt(Instant.now())
                .build();
        return repo.save(user);
    }
}
