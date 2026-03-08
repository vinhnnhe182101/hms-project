package com.product.hms.dto.response;

import com.product.hms.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String email;
    private Role role;
    private String provider;
    private Boolean isActive;
    private Long staffId;
    private Long customerId;
}
