package com.product.hms.service.impl;

import com.product.hms.dto.request.StaffRequestDTO;
import com.product.hms.dto.request.UserRequestDTO;
import com.product.hms.dto.response.StaffResponseDTO;
import com.product.hms.dto.response.UserResponseDTO;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Role;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.UserRepository;
import com.product.hms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements  UserDetailsService {

    private final UserRepository userRepository;





    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .disabled(!user.getIsActive())
                .build();
    }



}