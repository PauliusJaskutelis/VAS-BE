package com.fashiontrunk.fashiontrunkapi.Services;

import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity register(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with email already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setCreatedAt(new Date());
        user.setLastLogin(new Date());
        return userRepository.save(user);
    }

    public UserEntity login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        user.setLastLogin(new Date());
        return userRepository.save(user); // Optionally update last login
    }
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}
