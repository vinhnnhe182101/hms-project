package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

/**
 * Entity đại diện cho phân bổ thanh toán vào các khoản mục hóa đơn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID phân bổ</li>
 *   <li>{@link #paymentTransactionEntity} - Giao dịch thanh toán liên quan</li>
 *   <li>{@link #folioItemEntity} - Khoản mục hóa đơn liên quan</li>
 *   <li>{@link #amountApplied} - Số tiền phân bổ</li>
 *   <li>{@link #isActive} - Phân bổ còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "payment_allocation", schema = "hms_db")
public class PaymentAllocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransactionEntity paymentTransactionEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "folio_item_id", nullable = false)
    private FolioItemEntity folioItemEntity;

    @ColumnDefault("0.00")
    @Column(name = "amount_applied", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountApplied;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}