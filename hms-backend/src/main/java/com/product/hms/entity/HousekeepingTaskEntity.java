package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

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


}