package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

/**
 * Entity đại diện cho việc bàn giao tài sản cho nhân viên.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID bàn giao</li>
 *   <li>{@link #staffEntity} - Nhân viên nhận</li>
 *   <li>{@link #assetEntity} - Tài sản được bàn giao</li>
 *   <li>{@link #quantity} - Số lượng bàn giao</li>
 *   <li>{@link #handoverDate} - Ngày bàn giao</li>
 *   <li>{@link #isActive} - Bàn giao còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "asset_handover", schema = "hms_db")
public class AssetHandoverEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffEntity staffEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity assetEntity;

    @ColumnDefault("0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "handover_date", nullable = false)
    private Timestamp handoverDate;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}