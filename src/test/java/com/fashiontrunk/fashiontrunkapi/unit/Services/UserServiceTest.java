package com.fashiontrunk.fashiontrunkapi.unit.Services;

import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.UserRepository;
import com.fashiontrunk.fashiontrunkapi.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void register_successfullyCreatesUser() {
        String email = "test@example.com";
        String password = "password123";
        String name = "Test User";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        UserEntity registeredUser = userService.register(email, password, name);

        assertEquals(email, registeredUser.getEmail());
        assertEquals(name, registeredUser.getName());
        assertNotNull(registeredUser.getPasswordHash());
        assertNotNull(registeredUser.getCreatedAt());
        assertNotNull(registeredUser.getLastLogin());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_throwsException_whenEmailAlreadyExists() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(email, "password", "name");
        });

        assertEquals("User with email already exists", exception.getMessage());
    }

    @Test
    void login_successfulLoginUpdatesLastLogin() {
        String email = "test@example.com";
        String password = "password123";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password));
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        UserEntity loggedInUser = userService.login(email, password);

        assertEquals(email, loggedInUser.getEmail());
        assertNotNull(loggedInUser.getLastLogin());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void login_throwsException_whenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("notfound@example.com", "password");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void login_throwsException_whenPasswordInvalid() {
        String email = "test@example.com";
        String password = "password123";

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("differentPassword"));
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(email, password);
        });

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void findByEmail_returnsUser_whenExists() {
        String email = "findme@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserEntity> foundUser = userService.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }
}
