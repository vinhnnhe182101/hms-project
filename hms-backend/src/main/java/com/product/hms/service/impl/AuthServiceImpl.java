package com.product.hms.service.impl;

import com.product.hms.dto.request.LoginRequest;
import com.product.hms.dto.request.RegisterRequest;
import com.product.hms.dto.response.JwtResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Role;
import com.product.hms.exception.BadRequest;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.UserRepository;
import com.product.hms.security.JwtTokenProvider;
import com.product.hms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequest("User not found"));

        String fullName = getUserFullName(user);

        return JwtResponse.builder()
                .token(jwt)
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .fullName(fullName)
                .provider(user.getProvider())
                .build();
    }

    @Override
    @Transactional
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequest("Email already exists!");
        }

        // Create User
        UserEntity user = new UserEntity();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setProvider("LOCAL");
        user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : Role.CUSTOMER.name());
        user.setIsActive(true);

        UserEntity savedUser = userRepository.save(user);

        // Create Customer if role is CUSTOMER
        if (Role.CUSTOMER.name().equals(registerRequest.getRole())) {
            CustomerEntity customer = new CustomerEntity();
            customer.setUserEntity(savedUser);
            customer.setEmail(savedUser.getEmail());
            customer.setFullName(registerRequest.getFullName());
            customer.setPhoneNumber(registerRequest.getPhoneNumber());
            customer.setIsActive(true);
            customerRepository.save(customer);
        }

        // Authenticate and generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return JwtResponse.builder()
                .token(jwt)
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .fullName(registerRequest.getFullName())
                .provider(savedUser.getProvider())
                .build();
    }

    private String getUserFullName(UserEntity user) {
        if (user.getCustomerEntity() != null) {
            return user.getCustomerEntity().getFullName();
        } else if (user.getStaffEntity() != null) {
            return user.getStaffEntity().getFullName();
        }
        return user.getEmail();
    }
}
