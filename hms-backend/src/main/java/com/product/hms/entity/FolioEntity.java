package com.product.hms.entity;

import com.product.hms.enums.FolioStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "folio", schema = "hms_db")
public class FolioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reservation_room_allocation_id", nullable = false)
    private ReservationRoomAllocationEntity reservationRoomAllocation;

    @ColumnDefault("0.00")
    @Column(name = "total_charges", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCharges;

    @ColumnDefault("0.00")
    @Column(name = "total_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPaid;

    @ColumnDefault("0.00")
    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private FolioStatus status;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

}