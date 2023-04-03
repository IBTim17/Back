package com.ib.Tim17_Back.security;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class SaltGenerator {
    private static final int SALT_LENGTH = 16;

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}