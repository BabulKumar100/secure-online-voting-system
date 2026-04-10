package com.voting.service;

import com.voting.model.Candidate;
import com.voting.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    /**
     * Add new candidate
     */
    public String addCandidate(String name, String party, String symbol, String description) {
        // Check if candidate with same party already exists
        if (candidateRepository.existsByParty(party)) {
            throw new RuntimeException("Candidate with party '" + party + "' already exists");
        }

        Candidate candidate = new Candidate(name, party, symbol, description);
        candidateRepository.save(candidate);

        return "Candidate added successfully: " + name + " (" + party + ")";
    }

    /**
     * Get all candidates (Admin)
     */
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    /**
     * Get active candidates (For voting)
     */
    public List<Candidate> getActiveCandidates() {
        return candidateRepository.findByActiveTrue();
    }

    /**
     * Toggle candidate status (Active/Inactive)
     */
    public String toggleCandidateStatus(Long id) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);
        
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }

        Candidate candidate = candidateOpt.get();
        candidate.setActive(!candidate.isActive());
        candidateRepository.save(candidate);

        String status = candidate.isActive() ? "activated" : "deactivated";
        return "Candidate " + candidate.getName() + " has been " + status;
    }

    /**
     * Get candidate by ID
     */
    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
    }

    /**
     * Update candidate information
     */
    public String updateCandidate(Long id, String name, String party, String symbol, String description) {
        Candidate candidate = getCandidateById(id);

        // Check if party is being changed and new party already exists
        if (!candidate.getParty().equals(party) && candidateRepository.existsByParty(party)) {
            throw new RuntimeException("Candidate with party '" + party + "' already exists");
        }

        candidate.setName(name);
        candidate.setParty(party);
        candidate.setSymbol(symbol);
        candidate.setDescription(description);

        candidateRepository.save(candidate);

        return "Candidate updated successfully: " + name;
    }

    /**
     * Delete candidate (only if no votes cast)
     */
    public String deleteCandidate(Long id) {
        Candidate candidate = getCandidateById(id);
        
        // In production, check if candidate has votes before deletion
        // For now, we'll allow deletion
        
        candidateRepository.delete(candidate);
        
        return "Candidate deleted successfully: " + candidate.getName();
    }

    /**
     * Initialize default candidates (for demo)
     */
    public void initializeDefaultCandidates() {
        if (candidateRepository.count() == 0) {
            String[][] defaultCandidates = {
                {"Narendra Modi", "BJP", "Lotus", "Current Prime Minister of India"},
                {"Rahul Gandhi", "Congress", "Hand", "Leader of Opposition"},
                {"Arvind Kejriwal", "AAP", "Broom", "Delhi Chief Minister"},
                {"Mamata Banerjee", "TMC", "Flower", "West Bengal Chief Minister"}
            };

            for (String[] candidateData : defaultCandidates) {
                Candidate candidate = new Candidate(candidateData[0], candidateData[1], candidateData[2], candidateData[3]);
                candidateRepository.save(candidate);
            }
        }
    }
}
