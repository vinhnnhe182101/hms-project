package com.product.hms.service.impl;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.StaffAccountRequestDTO;
import com.product.hms.dto.request.StaffRequestDTO;
import com.product.hms.dto.response.*;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Department;
import com.product.hms.enums.Role;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.*;
import com.product.hms.repository.specification.CustomerSpecification;
import com.product.hms.repository.specification.StaffSpecification;
import com.product.hms.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    // Thêm các Dependency mới phục vụ cho việc lấy Reservation
    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final CustomerMapper customerMapper;

    public static final String DEFAULT_STAFF_PASSWORD = "Hms@HelloCacBan";

    public UserServiceImpl(UserRepository userRepository,
                           StaffRepository staffRepository,
                           CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder,
                           ReservationRepository reservationRepository,
                           ReservationRoomRepository reservationRoomRepository,
                           CustomerMapper customerMapper) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.reservationRepository = reservationRepository;
        this.reservationRoomRepository = reservationRoomRepository;
        this.customerMapper = customerMapper;
    }

    // ==========================================
    // USER & CUSTOMER MANAGEMENT
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        return toUserResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getCustomerUsersWithPagination(String email, Boolean isActive, Pageable pageable) {
        var spec = CustomerSpecification.build(email, isActive);
        Page<CustomerEntity> page = customerRepository.findAll(spec, pageable);

        return page.map(customer -> {
            if (customer.getUserEntity() != null) {
                return toUserResponseDTO(customer.getUserEntity());
            }
            // Fallback nếu Customer chưa có UserEntity
            return UserResponseDTO.builder()
                    .customerId(customer.getId())
                    .fullName(customer.getFullName())
                    .phoneNumber(customer.getPhoneNumber())
                    .identityCard(customer.getIdentityCard())
                    .email(customer.getEmail())
                    .role(Role.CUSTOMER)
                    .isActive(customer.getIsActive())
                    .reservations(new ArrayList<>())
                    .build();
        });
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

        String defaultPassword = "Hms@" + request.getPhoneNumber();

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(defaultPassword));
        userEntity.setRole(Role.STAFF.name());
        userEntity.setProvider("local");
        userEntity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        UserEntity savedUser = userRepository.save(userEntity);

        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setFullName(request.getFullName());
        staffEntity.setPhoneNumber(request.getPhoneNumber());
        staffEntity.setDepartment(Department.valueOf(request.getDepartment().toUpperCase(Locale.ROOT)));
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
        entity.setDepartment(Department.valueOf(request.getDepartment().toUpperCase(Locale.ROOT)));
        entity.setStatus(request.getStatus());

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
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

        entity.setIsActive(false);
        staffRepository.save(entity);

        if (entity.getUserEntity() != null) {
            entity.getUserEntity().setIsActive(false);
            userRepository.save(entity.getUserEntity());
        }
    }

    @Override
    public Page<StaffResponseDTO> getStaffWithPagination(String name, String email, String phoneNumber, String department, String status, Boolean isActive, Pageable pageable) {
        var spec = StaffSpecification.build(name, email, phoneNumber, department, status, isActive);
        Page<StaffEntity> page = staffRepository.findAll(spec, pageable);
        return page.map(this::toStaffResponseDTO);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    // ==========================================
    // MAPPERS & UTILS
    // ==========================================

    private UserResponseDTO toUserResponseDTO(UserEntity e) {
        Role role = null;
        if (e.getRole() != null) {
            try { role = Role.valueOf(e.getRole()); } catch (IllegalArgumentException ignored) {}
        }

        String fullName = null;
        String phoneNumber = null;
        String identityCard = null;
        Long customerId = null;
        List<ReservationResponse> reservations = new ArrayList<>();

        // Kiểm tra nếu là Customer thì lấy thông tin và reservations
        if (e.getCustomerEntity() != null) {
            CustomerEntity customer = e.getCustomerEntity();
            fullName = customer.getFullName();
            phoneNumber = customer.getPhoneNumber();
            identityCard = customer.getIdentityCard();
            customerId = customer.getId();

            // Tìm toàn bộ reservations của khách hàng này
            List<ReservationEntity> reservationEntities = reservationRepository.findByCustomerEntity(customer);
            if (reservationEntities != null && !reservationEntities.isEmpty()) {
                reservations = reservationEntities.stream()
                        .map(this::mapToReservationResponse) // Gọi hàm helper bên dưới
                        .collect(Collectors.toList());
            }
        }
        // Nếu là Staff thì lấy thông tin cơ bản
        else if (e.getStaffEntity() != null) {
            fullName = e.getStaffEntity().getFullName();
            phoneNumber = e.getStaffEntity().getPhoneNumber();
            // Staff không có CustomerID hay CCCD trong schema hiện tại
        }

        return UserResponseDTO.builder()
                .id(e.getId())
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .identityCard(identityCard)
                .email(e.getEmail())
                .role(role)
                .provider(e.getProvider())
                .isActive(e.getIsActive())
                .reservations(reservations) // Gắn list reservations vào đây
                .customerId(customerId)
                .build();
    }

    /**
     * Helper Method: Chuyển ReservationEntity sang ReservationResponse
     * Hàm này tái tạo lại logic map giống y hệt ở ReservationServiceImpl
     */
    private ReservationResponse mapToReservationResponse(ReservationEntity entity) {
        CustomerResponse customer = customerMapper.toResponse(entity.getCustomerEntity());

        List<RoomClassQuantityResponse> allocations = reservationRoomRepository
                .findByReservationEntity(entity)
                .stream()
                .map(allocation -> new RoomClassQuantityResponse(
                        allocation.getId(),
                        allocation.getRoomClassEntity() != null ? allocation.getRoomClassEntity().getId() : null,
                        allocation.getNumberOfPeople()))
                .toList();

        return new ReservationResponse(
                entity.getId(),
                entity.getCode(),
                customer,
                allocations,
                entity.getExpectedCheckIn(),
                entity.getExpectedCheckOut(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getNumberOfMembers(),
                entity.getNote(),
                entity.getCreatedAt());
    }

    private StaffResponseDTO toStaffResponseDTO(StaffEntity e) {
        return StaffResponseDTO.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .phoneNumber(e.getPhoneNumber())
                .department(e.getDepartment() != null ? e.getDepartment().name() : null)
                .status(e.getStatus())
                .isActive(e.getIsActive())
                .userId(e.getUserEntity() != null ? e.getUserEntity().getId() : null)
                .email(e.getUserEntity() != null ? e.getUserEntity().getEmail() : null)
                .build();
    }
}