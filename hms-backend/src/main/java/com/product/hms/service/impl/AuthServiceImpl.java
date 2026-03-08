package com.product.hms.service.impl;

import com.product.hms.dto.request.AuthRequest;
import com.product.hms.dto.response.AuthResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.repository.UserRepository;
import com.product.hms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        if (user.getCustomerEntity() == null) {
            throw new RuntimeException("Tài khoản này không có thông tin khách hàng");
        }

        CustomerEntity customer = user.getCustomerEntity();

        return new AuthResponse(
                customer.getId(),
                customer.getFullName(),
                user.getEmail(),
                customer.getPhoneNumber(),
                customer.getIdentityCard(),
                user.getRole()
        );
    }
}
