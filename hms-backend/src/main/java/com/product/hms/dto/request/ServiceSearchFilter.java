package com.product.hms.dto.request;

import com.product.hms.enums.ServiceCategory;

/**
 * DTO filter cho API search dịch vụ (Service).
 * <ul>
 *   <li>name: Tìm kiếm theo tên dịch vụ (LIKE, không phân biệt hoa thường).</li>
 *   <li>category: Lọc theo loại dịch vụ.</li>
 *   <li>status: Lọc theo trạng thái hoạt động (isActive).</li>
 * </ul>
 */
public record ServiceSearchFilter(
    String name,
    ServiceCategory category,
    Boolean status // true: active, false: inactive
) {}
