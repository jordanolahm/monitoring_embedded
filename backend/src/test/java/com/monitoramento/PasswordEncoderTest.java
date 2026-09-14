package com.monitoramento;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderTest {

    @Test
    void testAdminHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println("GENERATED_ADMIN123_HASH=" + hash);
        assertTrue(encoder.matches("admin123", hash));
    }
}
