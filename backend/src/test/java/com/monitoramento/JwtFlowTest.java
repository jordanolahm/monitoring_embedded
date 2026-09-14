package com.monitoramento;

import com.monitoramento.config.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JwtFlowTest {

    @Test
    void testJwtCreationAndValidation() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);

        UserDetails userDetails = new User(
                "admin",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        String extractedUser = jwtService.extractUsername(token);
        assertEquals("admin", extractedUser);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
}
