package com.voting.service;

import com.voting.model.User;
import com.voting.repository.UserRepository;
import com.voting.security.JwtUtil;
import com.voting.security.OtpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OtpService otpService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Register
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

    // Login with password verification
    public String login(User user) {
        String email = user.getEmail().trim().toLowerCase();

        User existingUser = repo.findFirstByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (encoder.matches(user.getPassword(), existingUser.getPassword())) {
            // Generate OTP for 2FA
            String otp = otpService.generateOtp(email);
            return "OTP sent to your email. Please verify to complete login.";
        }

        throw new RuntimeException("Invalid password");
    }

    // Verify OTP and complete authentication
    public String verifyOtpAndLogin(String email, String otp) {
        if (!otpService.verifyOtp(email, otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = repo.findFirstByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(user.getEmail());
    }

    // Generate OTP for password reset
    public String generateOtpForPasswordReset(String email) {
        User user = repo.findFirstByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return otpService.generateOtp(email);
    }

    // Verify OTP for password reset
    public boolean verifyOtpForPasswordReset(String email, String otp) {
        return otpService.verifyOtp(email, otp);
    }

    // Resend OTP
    public String resendOtp(String email) {
        if (!repo.existsByEmail(email.trim().toLowerCase())) {
            throw new RuntimeException("User not found");
        }

        return otpService.resendOtp(email);
    }
}