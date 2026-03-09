package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho khách hàng của khách sạn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID khách hàng</li>
 *   <li>{@link #fullName} - Họ tên</li>
 *   <li>{@link #phoneNumber} - Số điện thoại</li>
 *   <li>{@link #identityCard} - Số CMND/CCCD</li>
 *   <li>{@link #email} - Email</li>
 *   <li>{@link #type} - Loại khách hàng</li>
 *   <li>{@link #guardianEntity} - Người bảo hộ (nếu có)</li>
 *   <li>{@link #userEntity} - Tài khoản người dùng liên kết</li>
 *   <li>{@link #isActive} - Khách hàng còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "customer", schema = "hms_db")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "identity_card", length = 50)
    private String identityCard;

    @Column(name = "email")
    private String email;

    @Column(name = "type", length = 50)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "guardian_id")
    private CustomerEntity guardianEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "guardianEntity")
    private List<CustomerEntity> customerEntities = new ArrayList<>();

    @OneToMany(mappedBy = "customerEntity")
    private List<RatingEntity> ratingEntities = new ArrayList<>();

    @OneToMany(mappedBy = "customerEntity")
    private List<ReservationEntity> reservationEntities = new ArrayList<>();

    @OneToMany(mappedBy = "customerEntity")
    private List<RoomOccupantEntity> roomOccupantEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}