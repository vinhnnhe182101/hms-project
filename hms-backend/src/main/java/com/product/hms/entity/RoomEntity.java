package com.product.hms.entity;

import com.product.hms.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "room", schema = "hms_db")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_class_id", nullable = false)
    private RoomClassEntity roomClassEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private RoomStatus status;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "roomEntity")
    private List<DamageReportEntity> damageReportEntities = new ArrayList<>();

    @OneToMany(mappedBy = "roomEntity")
    private List<HousekeepingTaskEntity> housekeepingTaskEntities = new ArrayList<>();

    @OneToMany(mappedBy = "roomEntity")
    private List<ReservationRoomAllocationEntity> reservationRoomAllocationEntities = new ArrayList<>();

    @OneToMany(mappedBy = "roomEntity")
    private List<RoomAssetEntity> roomAssetEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}