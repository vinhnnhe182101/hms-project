package com.product.hms.entity;

import com.product.hms.enums.RoomAssetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entity đại diện cho tài sản gắn với phòng khách sạn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID tài sản phòng</li>
 *   <li>{@link #roomEntity} - Phòng liên quan</li>
 *   <li>{@link #assetEntity} - Tài sản liên quan</li>
 *   <li>{@link #quantity} - Số lượng</li>
 *   <li>{@link #status} - Trạng thái tài sản {@link com.product.hms.enums.RoomAssetStatus}</li>
 *   <li>{@link #isActive} - Tài sản phòng còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "room_asset", schema = "hms_db")
public class RoomAssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity assetEntity;

    @ColumnDefault("0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ColumnDefault("'Good'")
    @Column(name = "status", nullable = false, length = 20)
    private RoomAssetStatus status;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}