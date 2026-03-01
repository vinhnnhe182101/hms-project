package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

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


}