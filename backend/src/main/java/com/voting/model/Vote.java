package com.voting.model;

import com.voting.security.CryptographyService;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "vote", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "encrypted_data", nullable = false, columnDefinition = "TEXT")
    private String encryptedData;

    @Column(name = "iv", nullable = false)
    private String iv;

    @Column(name = "digital_signature", nullable = false)
    private String digitalSignature;

    @Column(name = "vote_hash", nullable = false)
    private String voteHash;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "signature_verified", nullable = false)
    private boolean signatureVerified = false;

    // Constructors
    public Vote() {}

    public Vote(User user, Candidate candidate, CryptographyService.EncryptedVote encryptedVote, String voteHash) {
        this.user = user;
        this.candidate = candidate;
        this.encryptedData = encryptedVote.getEncryptedData();
        this.iv = encryptedVote.getIv();
        this.digitalSignature = encryptedVote.getSignature();
        this.voteHash = voteHash;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public String getEncryptedData() { return encryptedData; }
    public void setEncryptedData(String encryptedData) { this.encryptedData = encryptedData; }

    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }

    public String getDigitalSignature() { return digitalSignature; }
    public void setDigitalSignature(String digitalSignature) { this.digitalSignature = digitalSignature; }

    public String getVoteHash() { return voteHash; }
    public void setVoteHash(String voteHash) { this.voteHash = voteHash; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isSignatureVerified() { return signatureVerified; }
    public void setSignatureVerified(boolean signatureVerified) { this.signatureVerified = signatureVerified; }
}