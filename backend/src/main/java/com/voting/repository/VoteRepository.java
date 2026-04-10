package com.voting.repository;

import com.voting.model.User;
import com.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByUser(User user);
    
    boolean existsByUser(User user);
    
    @Query("SELECT v FROM Vote v WHERE v.signatureVerified = true")
    List<Vote> findAllVerifiedVotes();
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.signatureVerified = true")
    long countVerifiedVotes();
    
    @Query("SELECT v.candidate.name, COUNT(v) FROM Vote v WHERE v.signatureVerified = true GROUP BY v.candidate.name")
    List<Object[]> getVoteResults();
}