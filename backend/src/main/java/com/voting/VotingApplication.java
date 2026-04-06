
package com.voting;

import com.voting.repository.VoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VotingApplication {
    public static void main(String[] args) {
        SpringApplication.run(VotingApplication.class, args);
    }

    @Bean
    public CommandLineRunner resetVotesOnStartup(VoteRepository voteRepository) {
        return args -> {
            voteRepository.deleteAll();
            System.out.println("Vote table cleared on startup");
        };
    }
}
