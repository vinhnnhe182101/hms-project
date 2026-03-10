package com.product.hms.entity;

import com.product.hms.enums.ServiceCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho dịch vụ khách sạn cung cấp.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID dịch vụ</li>
 *   <li>{@link #name} - Tên dịch vụ</li>
 *   <li>{@link #serviceCategory} - Loại dịch vụ {@link com.product.hms.enums.ServiceCategory}</li>
 *   <li>{@link #price} - Giá dịch vụ</li>
 *   <li>{@link #isActive} - Dịch vụ còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "service", schema = "hms_db")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "service_category", nullable = false, length = 50)
    private ServiceCategory serviceCategory;

    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "serviceEntity")
    private List<ServiceBookingEntity> serviceBookingEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}