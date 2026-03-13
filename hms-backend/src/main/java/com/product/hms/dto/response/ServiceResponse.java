package com.product.hms.dto.response;

import com.product.hms.enums.ServiceCategory;
import java.math.BigDecimal;

/**
 * DTO response cho API search dịch vụ (Service).
 * <ul>
 *   <li>id: ID dịch vụ.</li>
 *   <li>name: Tên dịch vụ.</li>
 *   <li>serviceCategory: Loại dịch vụ.</li>
 *   <li>price: Giá dịch vụ.</li>
 *   <li>isActive: Trạng thái hoạt động.</li>
 * </ul>
 */
public record ServiceResponse(
    Long id,
    String name,
    ServiceCategory serviceCategory,
    BigDecimal price,
    Boolean isActive
) {}
