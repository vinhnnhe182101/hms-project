package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "standard_capacity", nullable = false)
    private Integer standardCapacity;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "extra_person_fee", precision = 12, scale = 2)
    private BigDecimal extraPersonFee = BigDecimal.ZERO;

    private String description;

    @ColumnDefault("true")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms = new ArrayList<>();

    public boolean isActive() {
        return isActive != null && isActive;
    }

    public void setActive(boolean b) {

    }
}