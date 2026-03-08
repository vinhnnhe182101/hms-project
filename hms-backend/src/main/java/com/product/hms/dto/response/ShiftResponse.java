package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponse {

    private Long id;

    private String shiftName;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean isActive;
}
