package com.product.hms.entity;

import com.product.hms.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", unique = true, nullable = false, length = 20)
    private String roomNumber;          // 101, 202, 1001...

    @Column(nullable = false)
    private Integer floor;              // bắt buộc

    @Column(name = "room_sequence")
    private Integer roomSequence;       // 1 → phòng 01 của tầng

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;          // dùng RoomType thống nhất

    @Lob
    private String description;

    @ColumnDefault("true")
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Nếu cần relation từ RoomEntity cũ, thêm sau khi merge
    // @OneToMany(mappedBy = "room") private List<HousekeepingTaskEntity> tasks = new ArrayList<>();
}