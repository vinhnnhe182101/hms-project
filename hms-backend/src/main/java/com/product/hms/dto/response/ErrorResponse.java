package com.product.hms.dto.response;

import java.time.Instant;

/**
 * DTO phản hồi lỗi chuẩn cho tất cả API.
 *
 * @param code      Mã lỗi nghiệp vụ/hệ thống
 * @param message   Thông tin chi tiết lỗi
 * @param status    Mã trạng thái HTTP
 * @param path      Đường dẫn request
 * @param timestamp Thời điểm tạo phản hồi
 */
public record ErrorResponse(
        String code,
        String message,
        int status,
        String path,
        Instant timestamp
) {
}
