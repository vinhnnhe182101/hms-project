package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho tài sản trong khách sạn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID tài sản</li>
 *   <li>{@link #categoryEntity} - Loại tài sản</li>
 *   <li>{@link #name} - Tên tài sản</li>
 *   <li>{@link #totalQuantity} - Tổng số lượng</li>
 *   <li>{@link #availableQuantity} - Số lượng còn lại</li>
 *   <li>{@link #price} - Giá trị tài sản</li>
 *   <li>{@link #isActive} - Tài sản còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "asset", schema = "hms_db")
public class AssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategoryEntity categoryEntity;

    @Column(name = "name", nullable = false)
    private String name;

    @ColumnDefault("0")
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @ColumnDefault("0")
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "assetEntity")
    private List<AssetHandoverEntity> assetHandoverEntities = new ArrayList<>();

    @OneToMany(mappedBy = "assetEntity")
    private List<RoomAssetEntity> roomAssetEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}