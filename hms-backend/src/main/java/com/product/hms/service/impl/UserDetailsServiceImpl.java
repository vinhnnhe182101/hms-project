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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {
        return null;
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        return null;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return null;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return List.of();
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public UserResponseDTO updateUserRole(Long id, Role role) {
        return null;
    }

    @Override
    public UserResponseDTO updateUserStatus(Long id, Boolean isActive) {
        return null;
    }

    @Override
    public StaffResponseDTO createStaff(StaffRequestDTO request) {
        return null;
    }

    @Override
    public StaffResponseDTO updateStaff(Long id, StaffRequestDTO request) {
        return null;
    }

    @Override
    public StaffResponseDTO getStaffById(Long id) {
        return null;
    }

    @Override
    public List<StaffResponseDTO> getAllStaff() {
        return List.of();
    }

    @Override
    public void deleteStaff(Long id) {

    }

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

    @Override
    @Transactional
    public UserEntity processOAuth2User(Map<String, Object> attributes, String provider) {
        String email = (String) attributes.get("email");

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setProvider(provider);
                    newUser.setProviderId((String) attributes.get("sub"));
                    newUser.setRole(Role.CUSTOMER.name());
                    newUser.setIsActive(true);

                    UserEntity savedUser = userRepository.save(newUser);

                    // Create customer for OAuth2 user
                    CustomerEntity customer = new CustomerEntity();
                    customer.setUserEntity(savedUser);
                    customer.setEmail(savedUser.getEmail());
                    customer.setFullName((String) attributes.get("name"));
                    customer.setIsActive(true);
                    customerRepository.save(customer);

                    return savedUser;
                });

        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
