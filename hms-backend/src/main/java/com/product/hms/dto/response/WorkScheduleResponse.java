package com.product.hms.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleResponse {
    private Long id;

    private Long staffId;
    private String staffName;
    private String departmentName;

    private Long shiftId;
    private String shiftName;
    private LocalDate workDate;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;

    private String status;
}
