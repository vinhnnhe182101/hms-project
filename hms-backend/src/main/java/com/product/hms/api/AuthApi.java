package com.product.hms.api;

import com.product.hms.dto.request.LoginRequest;
import com.product.hms.dto.request.RegisterRequest;
import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.LoginResponse;
import com.product.hms.dto.response.RegisterResponse;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.repository.UserRepository;
import com.product.hms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthApi {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.registerUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    /**
     * Lấy thông tin profile của user đang đăng nhập (cần JWT Bearer token).
     * Frontend gọi endpoint này để lấy phoneNumber và identityCard từ DB.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerEntity customer = user.getCustomerEntity();
        if (customer == null) {
            return ResponseEntity.ok(ApiResponse.success("Profile loaded",
                    new CustomerResponse(null, user.getEmail(), null, null, user.getEmail(), null)));
        }

        CustomerResponse profile = new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getPhoneNumber(),
                customer.getIdentityCard(),
                customer.getEmail(),
                customer.getType()
        );

        return ResponseEntity.ok(ApiResponse.success("Profile loaded", profile));
    }
}