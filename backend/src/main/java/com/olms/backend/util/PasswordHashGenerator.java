package com.olms.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// made this class when creating the first Admin, to hash the password manually
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "testuser3@123";
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println("Hashed Password: " + hashedPassword);
    }
}