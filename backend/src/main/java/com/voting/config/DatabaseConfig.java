package com.voting.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        // Get the database URL from environment or use default
        String url = System.getenv().getOrDefault("DATABASE_URL", "jdbc:mysql://localhost:3306/voting_db");
        properties.setUrl(url);
        
        // Set driver based on URL
        if (url.contains("postgresql")) {
            properties.setDriverClassName("org.postgresql.Driver");
        } else {
            properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }
        
        // Set username from environment or default
        properties.setUsername(System.getenv().getOrDefault("DATABASE_USERNAME", "root"));
        
        // Set password from environment or default
        properties.setPassword(System.getenv().getOrDefault("DATABASE_PASSWORD", "Babulkumar@2004"));
        
        return properties;
    }
}
