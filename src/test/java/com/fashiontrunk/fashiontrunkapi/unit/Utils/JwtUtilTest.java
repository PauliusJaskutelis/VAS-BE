package com.fashiontrunk.fashiontrunkapi.unit.Utils;

import com.fashiontrunk.fashiontrunkapi.Util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateToken_shouldReturnNonNullToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";

        String token = JwtUtil.generateToken(userId, email);

        assertNotNull(token, "Generated token should not be null");
        assertFalse(token.isEmpty(), "Generated token should not be empty");
    }

    @Test
    void parseToken_shouldReturnValidClaims() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String token = JwtUtil.generateToken(userId, email);

        Claims claims = JwtUtil.parseToken(token);

        assertNotNull(claims, "Parsed claims should not be null");
        assertEquals(userId.toString(), claims.getSubject(), "Subject should match userId");
        assertEquals(email, claims.get("email"), "Email claim should match");
    }

    @Test
    void parseToken_withInvalidToken_shouldReturnNull() {
        String invalidToken = "this.is.not.a.valid.token";

        Claims claims = JwtUtil.parseToken(invalidToken);

        assertNull(claims, "Parsing invalid token should return null");
    }
}
