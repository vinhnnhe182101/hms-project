package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho ca làm việc của nhân viên.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID ca làm việc</li>
 *   <li>{@link #shiftName} - Tên ca</li>
 *   <li>{@link #startTime} - Thời gian bắt đầu</li>
 *   <li>{@link #endTime} - Thời gian kết thúc</li>
 *   <li>{@link #isActive} - Ca làm việc còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "shift", schema = "hms_db")
public class ShiftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "shift_name", nullable = false, length = 100)
    private String shiftName;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @OneToMany(mappedBy = "shiftEntity")
    private List<WorkScheduleEntity> workScheduleEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}