package com.product.hms.service;

import com.product.hms.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.product.hms.dto.request.StaffRequestDTO;
import com.product.hms.dto.request.UserRequestDTO;
import com.product.hms.dto.response.StaffResponseDTO;
import com.product.hms.dto.response.UserResponseDTO;
import com.product.hms.enums.Role;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO request);

    UserResponseDTO updateUser(Long id, UserRequestDTO request);
import java.util.Map;

    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getAllUsers();

    void deleteUser(Long id);

    UserResponseDTO updateUserRole(Long id, Role role);

    UserResponseDTO updateUserStatus(Long id, Boolean isActive);

    StaffResponseDTO createStaff(StaffRequestDTO request);

    StaffResponseDTO updateStaff(Long id, StaffRequestDTO request);

    StaffResponseDTO getStaffById(Long id);

    List<StaffResponseDTO> getAllStaff();

    void deleteStaff(Long id);
public interface UserService extends UserDetailsService {
    UserEntity processOAuth2User(Map<String, Object> attributes, String provider);
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
}
