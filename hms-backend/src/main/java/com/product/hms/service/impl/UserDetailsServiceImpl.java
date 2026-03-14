package com.product.hms.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product.hms.entity.UserEntity;
import com.product.hms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        log.info("User found - role: {}", user.getRole());

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Xử lý role dựa vào loại user
        if ("STAFF".equals(user.getRole())) {
            // Nếu là STAFF, lấy department từ staff entity
            if (user.getStaffEntity() != null) {
                String department = user.getStaffEntity().getDepartment().name();
                log.info("Staff department: {}", department);

                // Thêm department làm authority
                authorities.add(new SimpleGrantedAuthority(department));

                // Vẫn giữ role STAFF nếu cần
                authorities.add(new SimpleGrantedAuthority("STAFF"));
            } else {
                log.warn("Staff entity not found for user: {}", email);
                authorities.add(new SimpleGrantedAuthority("STAFF"));
            }
        } else {
            // ADMIN, CUSTOMER giữ nguyên
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
        }

        log.info("Authorities set: {}", authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() != null ? user.getPassword() : "",
                user.getIsActive() != null ? user.getIsActive() : true,
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }

}
