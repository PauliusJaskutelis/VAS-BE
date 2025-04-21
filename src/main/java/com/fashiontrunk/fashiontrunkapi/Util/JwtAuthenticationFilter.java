package com.fashiontrunk.fashiontrunkapi.Util;

import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID userId = UUID.fromString(claims.getSubject());
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity user = userOpt.get();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, null);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}