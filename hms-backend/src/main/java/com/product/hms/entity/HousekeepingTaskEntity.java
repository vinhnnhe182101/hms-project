package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

/**
 * Entity đại diện cho công việc vệ sinh phòng khách sạn.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID công việc</li>
 *   <li>{@link #roomEntity} - Phòng liên quan</li>
 *   <li>{@link #assigneeEntity} - Nhân viên được giao</li>
 *   <li>{@link #taskType} - Loại công việc</li>
 *   <li>{@link #status} - Trạng thái công việc</li>
 *   <li>{@link #assignedAt} - Thời điểm giao việc</li>
 *   <li>{@link #completedAt} - Thời điểm hoàn thành</li>
 *   <li>{@link #isActive} - Công việc còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "housekeeping_task", schema = "hms_db")
public class HousekeepingTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "assignee_id")
    private StaffEntity assigneeEntity;

    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "assigned_at")
    private Timestamp assignedAt;

    @Column(name = "completed_at")
    private Timestamp completedAt;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}