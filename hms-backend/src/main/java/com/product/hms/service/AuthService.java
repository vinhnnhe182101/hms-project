package com.product.hms.service;

import com.product.hms.dto.request.LoginRequest;
import com.product.hms.dto.request.RegisterRequest;
import com.product.hms.dto.response.LoginResponse;
import com.product.hms.dto.response.RegisterResponse;

public interface AuthService {
    LoginResponse authenticateUser(LoginRequest loginRequest);
    RegisterResponse registerUser(RegisterRequest registerRequest);
}