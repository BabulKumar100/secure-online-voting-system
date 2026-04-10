package com.voting.controller;

import com.voting.service.VoteService;
import com.voting.service.CandidateService;
import com.voting.security.CryptographyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:3000"})
public class AdminController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CryptographyService cryptographyService;

    /**
     * Get decrypted election results (Admin only)
     * RESULT DECRYPTION: Admin can decrypt and count votes
     */
    @GetMapping("/results")
    public ResponseEntity<?> getDecryptedResults() {
        try {
            Map<String, Long> results = voteService.getDecryptedResults();
            return ResponseEntity.ok(Map.of(
                "results", results,
                "totalVotes", voteService.getTotalVotesCount(),
                "message", "Results decrypted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify all vote signatures (Admin only)
     * INTEGRITY CHECK: Verify all digital signatures
     */
    @PostMapping("/verify-votes")
    public ResponseEntity<?> verifyAllVotes() {
        try {
            Map<String, Object> verification = voteService.verifyAllVotes();
            return ResponseEntity.ok(verification);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get system statistics (Admin only)
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getSystemStatistics() {
        try {
            Map<String, Object> stats = voteService.getVoteStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get public key for signature verification
     */
    @GetMapping("/public-key")
    public ResponseEntity<?> getPublicKey() {
        try {
            String publicKey = cryptographyService.getPublicKey();
            return ResponseEntity.ok(Map.of("publicKey", publicKey));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add new candidate (Admin only)
     */
    @PostMapping("/candidates")
    public ResponseEntity<?> addCandidate(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String party = request.get("party");
            String symbol = request.get("symbol");
            String description = request.get("description");

            String result = candidateService.addCandidate(name, party, symbol, description);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all candidates (Admin only)
     */
    @GetMapping("/candidates")
    public ResponseEntity<?> getAllCandidates() {
        try {
            return ResponseEntity.ok(candidateService.getAllCandidates());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Toggle candidate status (Admin only)
     */
    @PutMapping("/candidates/{id}/toggle")
    public ResponseEntity<?> toggleCandidateStatus(@PathVariable Long id) {
        try {
            String result = candidateService.toggleCandidateStatus(id);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get encryption system info (Admin only)
     */
    @GetMapping("/crypto-info")
    public ResponseEntity<?> getCryptoInfo() {
        try {
            Map<String, Object> info = Map.of(
                "algorithm", "AES-256-CBC",
                "signatureAlgorithm", "SHA256withRSA",
                "keySize", "256-bit AES, 2048-bit RSA",
                "systemStatus", "Secure",
                "features", Map.of(
                    "confidentiality", "Vote encryption enabled",
                    "integrity", "Digital signatures enabled",
                    "authentication", "OTP verification enabled"
                )
            );
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
