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

    /**
     * Base64 Data URL hoàn chỉnh, sẵn sàng dùng trực tiếp trong thẻ <img>.
     * Ví dụ: "data:image/jpeg;base64,/9j/4AAQSkZJRgAB..."
     */
    private String dataUrl;

    /**
     * MIME type: "image/jpeg", "image/png"...
     */
    private String imgType;

    /**
     * Có phải ảnh chính không
     */
    private Boolean isPrimary;
}
