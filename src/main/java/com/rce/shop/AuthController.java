package com.rce.shop;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public record LoginRequest(String username, String password) {}

    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest r) {
        String token = auth.login(r.username(), r.password());
        return Map.of("token", token, "username", r.username());
    }
}
