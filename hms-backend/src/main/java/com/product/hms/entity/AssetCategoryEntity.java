package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho loại tài sản trong khách sạn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID loại tài sản</li>
 *   <li>{@link #name} - Tên loại tài sản</li>
 *   <li>{@link #description} - Mô tả</li>
 *   <li>{@link #isActive} - Loại tài sản còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "asset_category", schema = "hms_db")
public class AssetCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "categoryEntity")
    private List<AssetEntity> assetEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}