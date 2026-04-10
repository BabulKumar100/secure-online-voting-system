package com.voting.security;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_MINUTES = 10;
    private final Map<String, OtpData> otpStorage = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    /**
     * Generate 6-digit OTP for user authentication
     * AUTHENTICATION: Only valid users can vote
     */
    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(OTP_VALIDITY_MINUTES);
        
        otpStorage.put(email, new OtpData(otp, expiryTime));
        
        // In production, send this OTP via email/SMS
        System.out.println("OTP for " + email + ": " + otp);
        
        return otp;
    }

    /**
     * Verify OTP for user authentication
     */
    public boolean verifyOtp(String email, String providedOtp) {
        OtpData otpData = otpStorage.get(email);
        
        if (otpData == null) {
            return false; // OTP not found
        }
        
        // Check if OTP has expired
        if (System.currentTimeMillis() > otpData.getExpiryTime()) {
            otpStorage.remove(email);
            return false; // OTP expired
        }
        
        // Verify OTP matches
        boolean isValid = otpData.getOtp().equals(providedOtp);
        
        if (isValid) {
            otpStorage.remove(email); // Remove OTP after successful verification
        }
        
        return isValid;
    }

    /**
     * Check if user has active OTP session
     */
    public boolean hasActiveOtp(String email) {
        OtpData otpData = otpStorage.get(email);
        return otpData != null && System.currentTimeMillis() <= otpData.getExpiryTime();
    }

    /**
     * Resend OTP (generate new one)
     */
    public String resendOtp(String email) {
        // Remove existing OTP if any
        otpStorage.remove(email);
        return generateOtp(email);
    }

    /**
     * Clear expired OTPs (cleanup method)
     */
    public void cleanupExpiredOtps() {
        long currentTime = System.currentTimeMillis();
        otpStorage.entrySet().removeIf(entry -> currentTime > entry.getValue().getExpiryTime());
    }

    // Inner class to store OTP data
    private static class OtpData {
        private final String otp;
        private final long expiryTime;

        public OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() { return otp; }
        public long getExpiryTime() { return expiryTime; }
    }
}
