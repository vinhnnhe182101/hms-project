package com.product.hms.dto.request;

import com.product.hms.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    private String provider = "local";

    private String providerId;

    private Boolean isActive = true;
}
