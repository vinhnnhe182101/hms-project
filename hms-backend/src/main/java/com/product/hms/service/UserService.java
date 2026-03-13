package com.product.hms.service;

import com.product.hms.dto.request.StaffAccountRequestDTO;
import com.product.hms.entity.UserEntity;
import com.product.hms.dto.request.StaffRequestDTO;
import com.product.hms.dto.request.UserRequestDTO;
import com.product.hms.dto.response.StaffResponseDTO;
import com.product.hms.dto.response.UserResponseDTO;
import com.product.hms.enums.Role;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUserRole(Long id, Role role);
    UserResponseDTO updateUserStatus(Long id, Boolean isActive);
    void deleteUser(Long id);

    // ==========================================
    // 2. STAFF MANAGEMENT (Full CRUD)
    // ==========================================
    List<StaffResponseDTO> getAllStaff();
    StaffResponseDTO getStaffById(Long id);
    StaffResponseDTO createStaffAccount(StaffAccountRequestDTO request);
    StaffResponseDTO updateStaff(Long id, StaffRequestDTO request);
    void deleteStaff(Long id);

    // ==========================================
    // 3. AUTH & UTILS
    // ==========================================
    UserEntity findByEmail(String email);

}
