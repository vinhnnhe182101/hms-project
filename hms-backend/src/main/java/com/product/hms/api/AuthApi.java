package com.product.hms.api;

import com.product.hms.dto.request.LoginRequest;
import com.product.hms.dto.request.RegisterRequest;
import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.JwtResponse;
import com.product.hms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthApi {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        JwtResponse jwtResponse = authService.registerUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", jwtResponse));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<ApiResponse> oauth2Success(@RequestParam String token,
                                                     @RequestParam String email,
                                                     @RequestParam String role) {
        return ResponseEntity.ok(ApiResponse.success("OAuth2 login successful",
                JwtResponse.builder()
                        .token(token)
                        .email(email)
                        .role(role)
                        .build()));
    }
}