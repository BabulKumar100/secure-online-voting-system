package com.voting.repository;

import com.voting.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    List<Candidate> findByActiveTrue();
    
    Optional<Candidate> findByParty(String party);
    
    boolean existsByParty(String party);
    
    List<Candidate> findByNameContainingIgnoreCase(String name);
}
