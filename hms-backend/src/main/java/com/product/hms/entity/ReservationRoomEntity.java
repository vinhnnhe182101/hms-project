package com.product.hms.entity;

import com.product.hms.enums.ReservationRoomStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entity đại diện cho thông tin đặt phòng cụ thể trong một đơn đặt phòng.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID đặt phòng chi tiết</li>
 *   <li>{@link #reservationEntity} - Đơn đặt phòng cha</li>
 *   <li>{@link #roomClassEntity} - Loại phòng đặt</li>
 *   <li>{@link #roomEntity} - Phòng thực tế</li>
 *   <li>{@link #priceAtBooking} - Giá tại thời điểm đặt</li>
 *   <li>{@link #numberOfPeople} - Số người ở</li>
 *   <li>{@link #status} - Trạng thái đặt phòng {@link com.product.hms.enums.ReservationRoomStatus}</li>
 *   <li>{@link #isActive} - Đặt phòng còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "reservation_room", schema = "hms_db")
public class ReservationRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservationEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_class_id", nullable = false)
    private RoomClassEntity roomClassEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "room_id")
    private RoomEntity roomEntity;

    @ColumnDefault("0.00")
    @Column(name = "price_at_booking", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAtBooking;

    @ColumnDefault("1")
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    @Column(name = "actual_check_out")
    private Instant actualCheckOut;

    @Column(name = "actual_check_in")
    private Instant actualCheckIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReservationRoomStatus status;

    @OneToMany(mappedBy = "reservationRoomEntity")
    private Set<RoomOccupantEntity> roomOccupantEntities = new LinkedHashSet<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}