package com.product.hms.entity;

import com.product.hms.enums.ServiceBookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservationEntity;

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

    @OneToMany(mappedBy = "serviceBookingEntity")
    private List<FolioItemEntity> folioItemEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}