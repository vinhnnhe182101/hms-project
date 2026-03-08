package com.product.hms.service;

import com.product.hms.dto.request.AuthRequest;
import com.product.hms.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
