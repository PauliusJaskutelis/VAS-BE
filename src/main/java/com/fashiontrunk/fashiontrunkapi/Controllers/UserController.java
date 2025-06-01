package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.UserService;
import com.fashiontrunk.fashiontrunkapi.Util.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

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

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String, String> payload) {
        String code = payload.get("token"); // This is actually the authorization code

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://oauth2.googleapis.com/token",
                    request,
                    Map.class
            );

            String idToken = (String) response.getBody().get("id_token");

            // ✅ Optional: validate and decode the ID token now
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) return ResponseEntity.status(401).body("Invalid ID token");

            GoogleIdToken.Payload tokenPayload = token.getPayload();
            String email = tokenPayload.getEmail();
            String name = (String) tokenPayload.get("name");

            UserEntity user = userService.findByEmail(email).orElseGet(() -> {
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setOauthProvider("google");
                newUser.setOauthId(tokenPayload.getSubject());
                return userService.save(newUser);
            });

            String jwt = JwtUtil.generateToken(user.getId(), user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "user", user
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Authentication failed");
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
