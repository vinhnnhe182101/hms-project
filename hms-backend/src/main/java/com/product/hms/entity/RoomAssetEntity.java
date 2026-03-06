package com.product.hms.entity;

import com.product.hms.enums.RoomAssetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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