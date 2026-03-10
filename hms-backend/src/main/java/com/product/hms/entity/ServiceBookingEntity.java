package com.product.hms.entity;

import com.product.hms.enums.ServiceBookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

/**
 * Entity đại diện cho việc đặt dịch vụ của khách trong quá trình lưu trú.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID đặt dịch vụ</li>
 *   <li>{@link #reservationRoomEntity} - Đặt phòng liên quan</li>
 *   <li>{@link #serviceEntity} - Dịch vụ liên quan</li>
 *   <li>{@link #quantity} - Số lượng dịch vụ</li>
 *   <li>{@link #status} - Trạng thái đặt dịch vụ {@link com.product.hms.enums.ServiceBookingStatus}</li>
 *   <li>{@link #priceAtBooking} - Giá tại thời điểm đặt</li>
 *   <li>{@link #isActive} - Đặt dịch vụ còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "service_booking", schema = "hms_db")
public class ServiceBookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reservation_room_id", nullable = false)
    private ReservationRoomEntity reservationRoomEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity serviceEntity;

    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ServiceBookingStatus status;

    @ColumnDefault("0.00")
    @Column(name = "price_at_booking", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAtBooking;


    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}