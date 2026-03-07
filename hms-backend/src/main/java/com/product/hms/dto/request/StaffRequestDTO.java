package com.product.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    private String fullName;

    @Size(max = 30)
    @Pattern(regexp = "^[0-9+\\-\\s]*$", message = "Phone number must contain only digits, +, - or spaces")
    private String phoneNumber;

    @Size(max = 100)
    private String department;

    @NotBlank(message = "Status is required")
    @Size(max = 50)
    private String status;

    private Boolean isActive = true;

    private Long userId;
}
