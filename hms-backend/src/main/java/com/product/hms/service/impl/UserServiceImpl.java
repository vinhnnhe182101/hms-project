package com.product.hms.service.impl;

import com.product.hms.dto.request.StaffAccountRequestDTO;
import com.product.hms.dto.request.StaffRequestDTO;
import com.product.hms.dto.request.UserRequestDTO;
import com.product.hms.dto.response.StaffResponseDTO;
import com.product.hms.dto.response.UserResponseDTO;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Department;
import com.product.hms.enums.Role;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.StaffRepository;
import com.product.hms.repository.UserRepository;
import com.product.hms.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public static final String DEFAULT_STAFF_PASSWORD = "Hms@HelloCacBan";

    public UserServiceImpl(UserRepository userRepository, StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        return toUserResponseDTO(entity);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserRole(Long id, Role role) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        entity.setRole(role.name());
        return toUserResponseDTO(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserStatus(Long id, Boolean isActive) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        entity.setIsActive(isActive);
        return toUserResponseDTO(userRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        entity.setIsActive(false); // Soft delete
        userRepository.save(entity);
    }

    // ==========================================
    // STAFF MANAGEMENT
    // ==========================================

    @Override
    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findAllByOrderByIdAsc().stream()
                .map(this::toStaffResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponseDTO getStaffById(Long id) {
        StaffEntity entity = staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND, "Staff not found with id: " + id));
        return toStaffResponseDTO(entity);
    }

    @Override
    @Transactional
    public StaffResponseDTO createStaffAccount(StaffAccountRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "Email already registered: " + request.getEmail());
        }

        // Tạo mật khẩu mặc định (Bảo mật cơ bản: Hms@ + Số điện thoại)
        String defaultPassword = "Hms@" + request.getPhoneNumber();

        // 1. Tạo User
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(defaultPassword));
        userEntity.setRole(Role.STAFF.name());
        userEntity.setProvider("local");
        userEntity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        UserEntity savedUser = userRepository.save(userEntity);

        // 2. Tạo Staff liên kết
        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setFullName(request.getFullName());
        staffEntity.setPhoneNumber(request.getPhoneNumber());
        staffEntity.setDepartment(Department.valueOf(request.getDepartment()));
        staffEntity.setStatus(request.getStatus());
        staffEntity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        staffEntity.setUserEntity(savedUser);

        return toStaffResponseDTO(staffRepository.save(staffEntity));
    }

    @Override
    @Transactional
    public StaffResponseDTO updateStaff(Long id, StaffRequestDTO request) {
        StaffEntity entity = staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND, "Staff not found with id: " + id));

        entity.setFullName(request.getFullName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setDepartment(Department.valueOf(request.getDepartment()));
        entity.setStatus(request.getStatus());

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
            // Cập nhật luôn trạng thái của User đăng nhập
            if (entity.getUserEntity() != null) {
                entity.getUserEntity().setIsActive(request.getIsActive());
                userRepository.save(entity.getUserEntity());
            }
        }

        return toStaffResponseDTO(staffRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteStaff(Long id) {
        StaffEntity entity = staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND, "Staff not found with id: " + id));

        entity.setIsActive(false); // Soft delete Staff
        staffRepository.save(entity);

        // Khóa luôn tài khoản User để không đăng nhập được nữa
        if (entity.getUserEntity() != null) {
            entity.getUserEntity().setIsActive(false);
            userRepository.save(entity.getUserEntity());
        }
    }

    // ==========================================
    // MAPPERS & UTILS
    // ==========================================

    private UserResponseDTO toUserResponseDTO(UserEntity e) {
        Role role = null;
        if (e.getRole() != null) {
            try { role = Role.valueOf(e.getRole()); } catch (IllegalArgumentException ignored) {}
        }
        return UserResponseDTO.builder()
                .id(e.getId())
                .email(e.getEmail())
                .role(role)
                .provider(e.getProvider())
                .isActive(e.getIsActive())
                .staffId(e.getStaffEntity() != null ? e.getStaffEntity().getId() : null)
                .customerId(e.getCustomerEntity() != null ? e.getCustomerEntity().getId() : null)
                .build();
    }

    private StaffResponseDTO toStaffResponseDTO(StaffEntity e) {
        return StaffResponseDTO.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .phoneNumber(e.getPhoneNumber())
                .department(e.getDepartment().toString())
                .status(e.getStatus())
                .isActive(e.getIsActive())
                .userId(e.getUserEntity() != null ? e.getUserEntity().getId() : null)
                .email(e.getUserEntity() != null ? e.getUserEntity().getEmail() : null)
                .build();
    }
    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
