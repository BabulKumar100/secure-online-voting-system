package com.voting.service;

import com.voting.model.Candidate;
import com.voting.model.User;
import com.voting.model.Vote;
import com.voting.repository.CandidateRepository;
import com.voting.repository.UserRepository;
import com.voting.repository.VoteRepository;
import com.voting.security.CryptographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CryptographyService cryptographyService;

    /**
     * Encrypt and save vote with digital signature
     * CONFIDENTIALITY + INTEGRITY: Vote is encrypted and signed
     */
    @Transactional
    public String saveVote(String userEmail, Long candidateId) throws Exception {
        User user = userRepository.findFirstByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has already voted
        if (voteRepository.existsByUser(user)) {
            throw new RuntimeException("You have already voted");
        }

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!candidate.isActive()) {
            throw new RuntimeException("Candidate is not active");
        }

        // Encrypt vote with digital signature
        CryptographyService.EncryptedVote encryptedVote = cryptographyService.encryptVote(
                candidate.getName(), user.getEmail()
        );

        // Generate hash for additional integrity
        String voteHash = cryptographyService.generateHash(candidate.getName() + user.getEmail());

        // Create and save encrypted vote
        Vote vote = new Vote(user, candidate, encryptedVote, voteHash);
        voteRepository.save(vote);

        return "Vote encrypted and saved successfully";
    }

    /**
     * Get decrypted results (Admin only)
     */
    public Map<String, Long> getDecryptedResults() {
        List<Vote> votes = voteRepository.findAllVerifiedVotes();
        Map<String, Long> results = new HashMap<>();

        for (Vote vote : votes) {
            try {
                // Create encrypted vote object from stored data
                CryptographyService.EncryptedVote encryptedVote = new CryptographyService.EncryptedVote(
                        vote.getEncryptedData(),
                        vote.getIv(),
                        vote.getDigitalSignature(),
                        vote.getCandidate().getName()
                );

                // Decrypt vote
                CryptographyService.DecryptedVote decryptedVote = cryptographyService.decryptVote(encryptedVote);

                // Only count votes with valid signatures
                if (decryptedVote.isSignatureValid()) {
                    String candidateName = decryptedVote.getCandidateName();
                    results.put(candidateName, results.getOrDefault(candidateName, 0L) + 1);
                }
            } catch (Exception e) {
                System.err.println("Error decrypting vote: " + e.getMessage());
            }
        }

        return results;
    }

    /**
     * Get all candidates for voting
     */
    public List<Candidate> getActiveCandidates() {
        return candidateRepository.findByActiveTrue();
    }

    /**
     * Check if user has voted
     */
    public boolean hasUserVoted(String userEmail) {
        User user = userRepository.findFirstByEmail(userEmail.trim().toLowerCase())
                .orElse(null);
        return user != null && voteRepository.existsByUser(user);
    }

    /**
     * Get total verified votes count
     */
    public long getTotalVotesCount() {
        return voteRepository.countVerifiedVotes();
    }

    /**
     * Verify all vote signatures (Admin function)
     */
    public Map<String, Object> verifyAllVotes() {
        List<Vote> votes = voteRepository.findAll();
        int validVotes = 0;
        int invalidVotes = 0;

        for (Vote vote : votes) {
            try {
                CryptographyService.EncryptedVote encryptedVote = new CryptographyService.EncryptedVote(
                        vote.getEncryptedData(),
                        vote.getIv(),
                        vote.getDigitalSignature(),
                        vote.getCandidate().getName()
                );

                CryptographyService.DecryptedVote decryptedVote = cryptographyService.decryptVote(encryptedVote);
                
                if (decryptedVote.isSignatureValid()) {
                    validVotes++;
                    vote.setSignatureVerified(true);
                } else {
                    invalidVotes++;
                }
            } catch (Exception e) {
                invalidVotes++;
            }
        }

        voteRepository.saveAll(votes);

        Map<String, Object> result = new HashMap<>();
        result.put("totalVotes", votes.size());
        result.put("validVotes", validVotes);
        result.put("invalidVotes", invalidVotes);
        result.put("verificationComplete", true);

        return result;
    }

    /**
     * Get vote statistics (Public)
     */
    public Map<String, Object> getVoteStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVotes", getTotalVotesCount());
        stats.put("candidatesCount", candidateRepository.findByActiveTrue().size());
        stats.put("systemStatus", "Secure");
        stats.put("encryptionEnabled", true);
        stats.put("digitalSignatures", true);
        return stats;
    }
}

