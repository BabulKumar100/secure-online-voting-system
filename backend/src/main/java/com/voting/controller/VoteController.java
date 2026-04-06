package com.voting.controller;

import com.voting.service.VoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:3000"})
public class VoteController {

    @Autowired
    private VoteService voteService;

    // 🗳️ Save vote in database
    @PostMapping("/vote")
    public String vote(@RequestBody Map<String, String> data) {
        String candidate = data.get("candidate");
        voteService.saveVote(candidate);
        return "Vote recorded for " + candidate;
    }

    // 📊 Get results from database
    @GetMapping("/results")
    public Map<String, Long> getResults() {
        return voteService.getResults();
    }

    // 🏠 Home API
    @GetMapping("/")
    public String home() {
        return "Backend is running!";
    }
}