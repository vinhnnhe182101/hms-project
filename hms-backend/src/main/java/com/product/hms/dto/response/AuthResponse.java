package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String identityCard;
    private String role;
}
