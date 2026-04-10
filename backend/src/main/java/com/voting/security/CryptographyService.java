package com.voting.security;

import org.springframework.stereotype.Service;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.*;

@Service
public class CryptographyService {

    // AES Configuration for vote encryption
    private static final String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_LENGTH = 16;
    
    // RSA Configuration for digital signatures
    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int RSA_KEY_SIZE = 2048;
    
    // Keys (In production, these should be stored securely)
    private final SecretKey aesKey;
    private final KeyPair rsaKeyPair;
    
    public CryptographyService() throws NoSuchAlgorithmException {
        // Generate AES key for encryption
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE);
        this.aesKey = keyGenerator.generateKey();
        
        // Generate RSA key pair for digital signatures
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        this.rsaKeyPair = keyPairGenerator.generateKeyPair();
    }

    /**
     * Encrypt vote using AES
     * CONFIDENTIALITY: Vote becomes unreadable to unauthorized users
     */
    public EncryptedVote encryptVote(String candidateName, String voterId) throws Exception {
        // Create random IV for each encryption
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        
        // Create vote data with timestamp and voter ID for integrity
        String voteData = candidateName + "|" + voterId + "|" + System.currentTimeMillis();
        byte[] encryptedData = cipher.doFinal(voteData.getBytes());
        
        // Generate digital signature for integrity
        String signature = generateDigitalSignature(voteData);
        
        return new EncryptedVote(
            Base64.getEncoder().encodeToString(encryptedData),
            Base64.getEncoder().encodeToString(iv),
            signature,
            candidateName
        );
    }

    /**
     * Decrypt vote using AES (Admin only)
     */
    public DecryptedVote decryptVote(EncryptedVote encryptedVote) throws Exception {
        byte[] iv = Base64.getDecoder().decode(encryptedVote.getIv());
        byte[] encryptedData = Base64.getDecoder().decode(encryptedVote.getEncryptedData());
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
        
        byte[] decryptedData = cipher.doFinal(encryptedData);
        String voteData = new String(decryptedData);
        
        // Parse vote data
        String[] parts = voteData.split("\\|");
        String candidateName = parts[0];
        String voterId = parts[1];
        long timestamp = Long.parseLong(parts[2]);
        
        // Verify digital signature for integrity
        boolean signatureValid = verifyDigitalSignature(voteData, encryptedVote.getSignature());
        
        return new DecryptedVote(candidateName, voterId, timestamp, signatureValid);
    }

    /**
     * Generate digital signature for integrity
     * INTEGRITY: Vote cannot be tampered with
     */
    private String generateDigitalSignature(String data) throws Exception {
        Signature signature = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
        signature.initSign(rsaKeyPair.getPrivate());
        signature.update(data.getBytes());
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * Verify digital signature
     */
    private boolean verifyDigitalSignature(String data, String signatureStr) throws Exception {
        Signature signature = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
        signature.initVerify(rsaKeyPair.getPublic());
        signature.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }

    /**
     * Get public key for verification (can be shared publicly)
     */
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(rsaKeyPair.getPublic().getEncoded());
    }

    /**
     * Generate hash for additional integrity check
     */
    public String generateHash(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    // Inner classes for structured data
    public static class EncryptedVote {
        private final String encryptedData;
        private final String iv;
        private final String signature;
        private final String originalCandidate;

        public EncryptedVote(String encryptedData, String iv, String signature, String originalCandidate) {
            this.encryptedData = encryptedData;
            this.iv = iv;
            this.signature = signature;
            this.originalCandidate = originalCandidate;
        }

        // Getters
        public String getEncryptedData() { return encryptedData; }
        public String getIv() { return iv; }
        public String getSignature() { return signature; }
        public String getOriginalCandidate() { return originalCandidate; }
    }

    public static class DecryptedVote {
        private final String candidateName;
        private final String voterId;
        private final long timestamp;
        private final boolean signatureValid;

        public DecryptedVote(String candidateName, String voterId, long timestamp, boolean signatureValid) {
            this.candidateName = candidateName;
            this.voterId = voterId;
            this.timestamp = timestamp;
            this.signatureValid = signatureValid;
        }

        // Getters
        public String getCandidateName() { return candidateName; }
        public String getVoterId() { return voterId; }
        public long getTimestamp() { return timestamp; }
        public boolean isSignatureValid() { return signatureValid; }
    }
}
