package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

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


}