package com.product.hms.service.impl;

import com.product.hms.dto.request.StaffRequestDTO;
import com.product.hms.dto.request.UserRequestDTO;
import com.product.hms.dto.response.StaffResponseDTO;
import com.product.hms.dto.response.UserResponseDTO;
import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Role;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.StaffRepository;
import com.product.hms.repository.UserRepository;
import com.product.hms.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    public UserServiceImpl(UserRepository userRepository, StaffRepository staffRepository) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "Email already registered: " + request.getEmail());
        }
        UserEntity entity = new UserEntity();
        mapUserRequestToEntity(request, entity);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPassword(request.getPassword());
        }
        entity.setProvider(request.getProvider() != null ? request.getProvider() : "local");
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        UserEntity saved = userRepository.save(entity);
        return toUserResponseDTO(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        if (!entity.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "Email already registered: " + request.getEmail());
        }
        mapUserRequestToEntity(request, entity);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPassword(request.getPassword());
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
        UserEntity saved = userRepository.save(entity);
        return toUserResponseDTO(saved);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        return toUserResponseDTO(entity);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        entity.setIsActive(false);
        userRepository.save(entity);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserRole(Long id, Role role) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        entity.setRole(role.name());
        UserEntity saved = userRepository.save(entity);
        return toUserResponseDTO(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserStatus(Long id, Boolean isActive) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        entity.setIsActive(isActive);
        UserEntity saved = userRepository.save(entity);
        return toUserResponseDTO(saved);
    }

    @Override
    @Transactional
    public StaffResponseDTO createStaff(StaffRequestDTO request) {
        StaffEntity entity = new StaffEntity();
        mapStaffRequestToEntity(request, entity);
        if (request.getUserId() != null) {
            UserEntity user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + request.getUserId()));
            entity.setUserEntity(user);
        }
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        StaffEntity saved = staffRepository.save(entity);
        return toStaffResponseDTO(saved);
    }

    @Override
    @Transactional
    public StaffResponseDTO updateStaff(Long id, StaffRequestDTO request) {
        StaffEntity entity = staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND, "Staff not found with id: " + id));
        mapStaffRequestToEntity(request, entity);
        if (request.getUserId() != null) {
            UserEntity user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + request.getUserId()));
            entity.setUserEntity(user);
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
        StaffEntity saved = staffRepository.save(entity);
        return toStaffResponseDTO(saved);
    }

    @Override
    public StaffResponseDTO getStaffById(Long id) {
        StaffEntity entity = staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND, "Staff not found with id: " + id));
        return toStaffResponseDTO(entity);
    }

    @Override
    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findAllByOrderByIdAsc().stream()
                .map(this::toStaffResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteStaff(Long id) {
        StaffEntity entity = staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND, "Staff not found with id: " + id));
        entity.setIsActive(false);
        staffRepository.save(entity);
    }

    private void mapUserRequestToEntity(UserRequestDTO request, UserEntity entity) {
        entity.setEmail(request.getEmail());
        entity.setRole(request.getRole() != null ? request.getRole().name() : entity.getRole());
        if (request.getProvider() != null) {
            entity.setProvider(request.getProvider());
        }
        if (request.getProviderId() != null) {
            entity.setProviderId(request.getProviderId());
        }
    }

    private UserResponseDTO toUserResponseDTO(UserEntity e) {
        Role role = null;
        if (e.getRole() != null) {
            try {
                role = Role.valueOf(e.getRole());
            } catch (IllegalArgumentException ignored) {
            }
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

    private void mapStaffRequestToEntity(StaffRequestDTO request, StaffEntity entity) {
        entity.setFullName(request.getFullName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setDepartment(request.getDepartment());
        entity.setStatus(request.getStatus());
    }

    private StaffResponseDTO toStaffResponseDTO(StaffEntity e) {
        String email = null;
        Long userId = null;
        if (e.getUserEntity() != null) {
            userId = e.getUserEntity().getId();
            email = e.getUserEntity().getEmail();
        }
        return StaffResponseDTO.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .phoneNumber(e.getPhoneNumber())
                .department(e.getDepartment())
                .status(e.getStatus())
                .isActive(e.getIsActive())
                .userId(userId)
                .email(email)
                .build();
    }
}
