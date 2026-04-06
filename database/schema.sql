CREATE DATABASE voting_db;

USE voting_db;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    UNIQUE KEY unique_email (email)
);

CREATE TABLE vote (
    id INT AUTO_INCREMENT PRIMARY KEY,
    candidate VARCHAR(50)
);