package com.product.hms.entity;

import com.product.hms.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho đơn đặt phòng khách sạn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID đơn đặt phòng</li>
 *   <li>{@link #code} - Mã code đặt phòng</li>
 *   <li>{@link #customerEntity} - Khách hàng đặt phòng</li>
 *   <li>{@link #expectedCheckIn} - Thời gian dự kiến nhận phòng</li>
 *   <li>{@link #expectedCheckOut} - Thời gian dự kiến trả phòng</li>
 *   <li>{@link #status} - Trạng thái đơn đặt phòng {@link com.product.hms.enums.ReservationStatus}</li>
 *   <li>{@link #totalDeposit} - Tổng tiền cọc</li>
 *   <li>{@link #numberOfMembers} - Số thành viên</li>
 *   <li>{@link #isActive} - Đơn đặt phòng còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@FieldNameConstants
@Table(name = "reservation", schema = "hms_db")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerEntity;

    @Column(name = "expected_check_in", nullable = false)
    private Timestamp expectedCheckIn;

    @Column(name = "expected_check_out", nullable = false)
    private Timestamp expectedCheckOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReservationStatus status;

    @ColumnDefault("0.00")
    @Column(name = "total_deposit", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDeposit;


    @ColumnDefault("1")
    @Column(name = "number_of_members", nullable = false)
    private Integer numberOfMembers;

    @Lob
    @Column(name = "note")
    private String note;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "reservationEntity")
    private List<DamageReportEntity> damageReportEntities = new ArrayList<>();

    @OneToMany(mappedBy = "reservationEntity")
    private List<RatingEntity> ratingEntities = new ArrayList<>();

    @OneToMany(mappedBy = "reservationEntity")
    private List<ReservationRoomEntity> reservationRoomEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}