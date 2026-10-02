package com.rce.shop;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/** Simple in-memory token login. Exercise: replace with Spring Security + JWT. */
@Service
public class AuthService {
    public static final String HEADER = "X-Auth-Token";
    private final Map<String, String> users = Map.of("student", "student123", "admin", "admin123");
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    public String login(String username, String password) {
        if (username == null || password == null || !password.equals(users.get(username))) {
            throw new ApiException(401, "Invalid username or password");
        }
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    public String userFor(String token) {
        String user = token == null ? null : tokens.get(token);
        if (user == null) throw new ApiException(401, "Please login first");
        return user;
    }
}
