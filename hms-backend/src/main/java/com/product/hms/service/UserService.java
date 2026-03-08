package com.product.hms.service;

import com.product.hms.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface UserService extends UserDetailsService {
    UserEntity processOAuth2User(Map<String, Object> attributes, String provider);
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
}