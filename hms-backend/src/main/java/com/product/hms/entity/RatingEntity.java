package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

/**
 * Entity đại diện cho đánh giá của khách hàng về dịch vụ/phòng.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID đánh giá</li>
 *   <li>{@link #reservationEntity} - Đơn đặt phòng liên quan</li>
 *   <li>{@link #customerEntity} - Khách hàng đánh giá</li>
 *   <li>{@link #rating} - Điểm đánh giá</li>
 *   <li>{@link #comment} - Nhận xét</li>
 *   <li>{@link #reviewDate} - Ngày đánh giá</li>
 *   <li>{@link #isPublic} - Công khai đánh giá</li>
 *   <li>{@link #isActive} - Đánh giá còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "rating", schema = "hms_db")
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservationEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerEntity;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Lob
    @Column(name = "comment")
    private String comment;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "review_date", nullable = false)
    private Timestamp reviewDate;


    @ColumnDefault("1")
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}