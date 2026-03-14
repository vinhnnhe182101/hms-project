package com.product.hms.dto.response;

import com.product.hms.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String identityCard;
    private String email;
    private Role role;
    private String provider;
    private Boolean isActive;
    private List<ReservationResponse> reservations;
    private Long customerId;
}
