package com.product.hms.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDTO {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String department;
    private String status;
    private Boolean isActive;
    private Long userId;
    private String email;
}
