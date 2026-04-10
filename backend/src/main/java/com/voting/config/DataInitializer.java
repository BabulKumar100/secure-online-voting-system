package com.voting.config;

import com.voting.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CandidateService candidateService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default candidates when application starts
        candidateService.initializeDefaultCandidates();
        System.out.println("Default candidates initialized successfully");
    }
}
