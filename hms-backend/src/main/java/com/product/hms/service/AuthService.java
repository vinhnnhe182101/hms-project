package com.product.hms.service;

import com.product.hms.dto.request.LoginRequest;
import com.product.hms.dto.request.RegisterRequest;
import com.product.hms.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse registerUser(RegisterRequest registerRequest);
}
