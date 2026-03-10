package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

/**
 * Entity đại diện cho lịch làm việc của nhân viên.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID lịch làm việc</li>
 *   <li>{@link #staffEntity} - Nhân viên</li>
 *   <li>{@link #shiftEntity} - Ca làm việc</li>
 *   <li>{@link #workDate} - Ngày làm việc</li>
 *   <li>{@link #status} - Trạng thái lịch</li>
 *   <li>{@link #isActive} - Lịch còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "work_schedule", schema = "hms_db")
public class WorkScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffEntity staffEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftEntity shiftEntity;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @ColumnDefault("'SCHEDULED'")
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

}