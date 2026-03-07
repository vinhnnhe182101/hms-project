package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "room_img", schema = "hms_db")
public class RoomImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "room_class_id", nullable = false)
    private RoomClassEntity roomClassEntity;

    @Column(name = "img_url", nullable = false, length = 2048)
    private String imgUrl;

    @Column(name = "img_type", length = 50)
    private String imgType;

    @ColumnDefault("0")
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}