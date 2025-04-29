package com.fashiontrunk.fashiontrunkapi.integration.Config;

import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.UUID;

@TestConfiguration
public class TestAuthenticationConfig {

    @Bean
    public Authentication testAuthentication() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("testuser@test.com");
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }
}
