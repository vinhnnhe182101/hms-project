package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomImgResponse {

    private Long id;
    private String dataUrl;
    private String imgType;
    private Boolean isPrimary;
}
