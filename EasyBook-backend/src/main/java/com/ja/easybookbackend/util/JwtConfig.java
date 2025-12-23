package com.ja.easybookbackend.util;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;

@Component
public class JwtConfig {
    private static Key SECRET_KEY;

    @Value("${jwt.secret:EasyBookDevSecretPleaseChange1234567890}")
    private String secret;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            try {
                MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
                keyBytes = sha512.digest(keyBytes);
            } catch (Exception ignored) {
                byte[] padded = new byte[64];
                System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 64));
                for (int i = keyBytes.length; i < 64; i++) {
                    padded[i] = (byte) (i * 31);
                }
                keyBytes = padded;
            }
        }
        SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    public static Key getSecretKey() {
        return SECRET_KEY;
    }
}
