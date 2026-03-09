package com.product.hms.dto.response;

import com.product.hms.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Staff response data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private Long id;

    private String fullName;

    private Department department;

    private String status;
}
