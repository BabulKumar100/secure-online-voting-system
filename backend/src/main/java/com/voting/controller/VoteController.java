package com.voting.controller;

import com.voting.service.VoteService;
import com.voting.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:3000"})
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get voting candidates
     */
    @GetMapping("/candidates")
    public ResponseEntity<?> getCandidates() {
        try {
            return ResponseEntity.ok(voteService.getActiveCandidates());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Submit encrypted vote
     * VOTE ENCRYPTION: Vote is encrypted before storage
     */
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        try {
            // Extract user email from JWT token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String userEmail = jwtUtil.extractUsername(token);
                
                Long candidateId = Long.parseLong(data.get("candidateId").toString());
                
                String result = voteService.saveVote(userEmail, candidateId);
                return ResponseEntity.ok(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Authentication required"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check if user has voted
     */
    @GetMapping("/has-voted")
    public ResponseEntity<?> hasUserVoted(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String userEmail = jwtUtil.extractUsername(token);
                
                boolean hasVoted = voteService.hasUserVoted(userEmail);
                return ResponseEntity.ok(Map.of("hasVoted", hasVoted));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Authentication required"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get public vote statistics (no decryption)
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = voteService.getVoteStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get voting system status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getVotingStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("system", "Secure Online Voting System");
            status.put("version", "2.0");
            status.put("security", "Enabled");
            status.put("encryption", "AES-256");
            status.put("digitalSignatures", "RSA-2048");
            status.put("authentication", "OTP + JWT");
            status.put("status", "Operational");
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Home API
     */
    @GetMapping("/")
    public String home() {
        return "Secure Online Voting System - Backend is running!";
    }
}