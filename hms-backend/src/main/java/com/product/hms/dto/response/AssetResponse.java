package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {
    private Long id;
    private String name;
    private String categoryName;
    private Integer quantity;   // Số lượng asset gắn cho phòng (từ RoomAsset)
    private String status;      // Tình trạng asset trong phòng
}
