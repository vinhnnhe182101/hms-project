package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entity đại diện cho người ở thực tế trong phòng khách sạn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID người ở</li>
 *   <li>{@link #reservationRoomEntity} - Đặt phòng cụ thể</li>
 *   <li>{@link #customerEntity} - Khách hàng lưu trú</li>
 *   <li>{@link #role} - Vai trò của người ở</li>
 *   <li>{@link #isActive} - Trạng thái hoạt động</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "room_occupant", schema = "hms_db")
public class RoomOccupantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reservation_room_id", nullable = false)
    private ReservationRoomEntity reservationRoomEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerEntity;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}