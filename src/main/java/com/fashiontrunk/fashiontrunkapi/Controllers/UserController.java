package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.UserService;
import com.fashiontrunk.fashiontrunkapi.Util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");
            String name = payload.get("name");

            UserEntity user = userService.register(email, password, name);
            String token = JwtUtil.generateToken(user.getId(), user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");

            UserEntity user = userService.login(email, password);
            String token = JwtUtil.generateToken(user.getId(), user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", user
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null)
            return ResponseEntity.status(401).body("Unauthorized");
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }
}
