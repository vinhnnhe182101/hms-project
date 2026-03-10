package com.product.hms.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;

    public UserServiceImpl(UserRepository userRepository, StaffRepository staffRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.customerRepository = customerRepository;
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
        entity.setDepartment(Department.valueOf(request.getDepartment()));
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
                .department(e.getDepartment().toString())
                .status(e.getStatus())
                .isActive(e.getIsActive())
                .userId(userId)
                .email(email)
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
