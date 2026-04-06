package com.voting.service;

import com.voting.model.User;
import com.voting.repository.UserRepository;
import com.voting.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ✅ Register
    public String register(User user) {
        String email = user.getEmail().trim().toLowerCase();

        if (repo.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        user.setEmail(email);
        user.setPassword(encoder.encode(user.getPassword()));

        repo.save(user);

        return "User Registered Successfully";
    }

    // ✅ Login
    public String login(User user) {
        String email = user.getEmail().trim().toLowerCase();

        User existingUser = repo.findFirstByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (encoder.matches(user.getPassword(), existingUser.getPassword())) {
            return jwtUtil.generateToken(existingUser.getEmail());
        }

        throw new RuntimeException("Invalid password");
    }
}