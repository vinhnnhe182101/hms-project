-- =========================================================
-- HMS DB - MySQL 8 Full Schema (snake_case) + enum comments
-- DB name: hms_db
--
-- Mục tiêu:
-- 1) Tạo mới DB + toàn bộ bảng (DROP/CREATE)
-- 2) Thêm cột is_active cho mọi bảng (default 1)
-- 3) Dùng VARCHAR/ENUM như hiện tại của bạn
-- 4) COMMENT rõ ràng các cột status/type/role nhận giá trị nào
--
-- Lưu ý:
-- - Đây là "full schema": chạy 1 phát là có DB sạch.
-- - COMMENT chỉ mang tính tài liệu (DB không tự enforce nếu cột là VARCHAR).
--   Nếu muốn enforce, bạn đổi sang ENUM hoặc CHECK.
-- =========================================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

DROP DATABASE IF EXISTS `hms_db`;
CREATE DATABASE `hms_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `hms_db`;

-- =========================
-- room_class
-- =========================
CREATE TABLE `room_class`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`              VARCHAR(255)    NOT NULL,
    `base_price`        DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `standard_capacity` INT             NOT NULL DEFAULT 1,
    `max_capacity`      INT             NOT NULL DEFAULT 1,
    `extra_person_fee`  DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`         TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- room
-- =========================
CREATE TABLE `room`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_number`   VARCHAR(50)     NOT NULL,
    `room_class_id` BIGINT UNSIGNED NOT NULL,
    `status`        VARCHAR(50)     NOT NULL COMMENT 'Room status: AVAILABLE | RESERVED | CLEAN | DIRTY | OCCUPIED | MAINTENANCE',
    `description`   TEXT            NULL,
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_room_number` (`room_number`),
    KEY `idx_room_room_class_id` (`room_class_id`),
    CONSTRAINT `fk_room_room_class`
        FOREIGN KEY (`room_class_id`) REFERENCES `room_class` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- user
-- =========================
CREATE TABLE `user`
(
    `id`          BIGINT UNSIGNED         NOT NULL AUTO_INCREMENT,
    `email`       VARCHAR(255)            NOT NULL,
    `password`    VARCHAR(255)            NULL,
    `role`        VARCHAR(50)             NOT NULL COMMENT 'User role (suggested): ADMIN | RECEPTIONIST | HOUSEKEEPING | MANAGER | CUSTOMER',
    `provider`    ENUM ('local','google') NOT NULL DEFAULT 'local' COMMENT 'Auth provider ENUM: local | google',
    `provider_id` VARCHAR(255)            NULL,
    `is_active`   TINYINT(1)              NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- customer
-- =========================
CREATE TABLE `customer`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `full_name`     VARCHAR(255)    NOT NULL,
    `phone_number`  VARCHAR(30)     NULL,
    `identity_card` VARCHAR(50)     NULL,
    `email`         VARCHAR(255)    NULL,
    `type`          VARCHAR(50)     NULL COMMENT 'Customer type (suggested): ADULT | CHILD | VIP | CORPORATE (tuỳ bạn chuẩn hoá)',
    `guardian_id`   BIGINT UNSIGNED NULL,
    `user_id`       BIGINT UNSIGNED NULL,
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_customer_guardian_id` (`guardian_id`),
    KEY `idx_customer_user_id` (`user_id`),
    CONSTRAINT `fk_customer_guardian`
        FOREIGN KEY (`guardian_id`) REFERENCES `customer` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT `fk_customer_user`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- staff
-- =========================
CREATE TABLE `staff`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT UNSIGNED NULL,
    `department`   VARCHAR(100)    NULL,
    `full_name`    VARCHAR(255)    NOT NULL,
    `phone_number` VARCHAR(30)     NULL,
    `status`       VARCHAR(50)     NOT NULL COMMENT 'Staff status (suggested): ACTIVE | INACTIVE | SUSPENDED',
    `is_active`    TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_staff_user_id` (`user_id`),
    CONSTRAINT `fk_staff_user`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- reservation
-- =========================
CREATE TABLE `reservation`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `code`               VARCHAR(50)     NOT NULL,
    `customer_id`        BIGINT UNSIGNED NOT NULL,
    `expected_check_in`  DATETIME        NOT NULL,
    `expected_check_out` DATETIME        NOT NULL,
    `status`             VARCHAR(50)     NOT NULL COMMENT 'Reservation status: PENDING_DEPOSIT | CONFIRMED | CANCELLED | IN_HOUSE | CHECKED_OUT | FINISHED',
    `total_deposit`      DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `number_of_members`  INT             NOT NULL DEFAULT 1,
    `note`               TEXT            NULL,
    `created_at`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_active`          TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reservation_code` (`code`),
    KEY `idx_reservation_customer_id` (`customer_id`),
    CONSTRAINT `fk_reservation_customer`
        FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- reservation_room_allocation
-- =========================
CREATE TABLE `reservation_room_allocation`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_id`   BIGINT UNSIGNED NOT NULL,
    `room_class_id`    BIGINT UNSIGNED NOT NULL,
    `room_id`          BIGINT UNSIGNED NULL,
    `status`           VARCHAR(50)     NOT NULL DEFAULT 'PENDING'
        COMMENT 'Allocation status: PENDING | ASSIGNED | CHECKED_IN | CHECKED_OUT | CANCELLED',
    `quantity`         INT             NOT NULL DEFAULT 1 COMMENT 'Quantity of rooms in this allocation (usually 1 when room_id is used).',
    `price_at_booking` DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `actual_check_out` DATETIME        NULL,
    `is_active`        TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_reservation_detail_reservation_id` (`reservation_id`),
    KEY `idx_reservation_detail_room_class_id` (`room_class_id`),
    KEY `idx_reservation_detail_room_id` (`room_id`),
    KEY `idx_reservation_detail_status` (`status`),
    CONSTRAINT `fk_reservation_detail_reservation`
        FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_reservation_detail_room_class`
        FOREIGN KEY (`room_class_id`) REFERENCES `room_class` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_reservation_detail_room`
        FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- room_occupant
-- =========================
CREATE TABLE `room_occupant`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `allocation_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> reservation_room_allocation.id',
    `customer_id`   BIGINT UNSIGNED NOT NULL,
    `role`          VARCHAR(50)     NOT NULL COMMENT 'Occupant role: PRIMARY | GUEST | CHILD',
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_room_occupant_allocation_id` (`allocation_id`),
    KEY `idx_room_occupant_customer_id` (`customer_id`),
    CONSTRAINT `fk_room_occupant_allocation`
        FOREIGN KEY (`allocation_id`) REFERENCES `reservation_room_allocation` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_room_occupant_customer`
        FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- service
-- =========================
CREATE TABLE `service`
(
    `id`               BIGINT UNSIGNED              NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(255)                 NOT NULL,
    `service_category` ENUM ('Spa','Minibar','F&B') NOT NULL COMMENT 'Service category ENUM: Spa | Minibar | F&B',
    `price`            DECIMAL(12, 2)               NOT NULL DEFAULT 0,
    `is_active`        TINYINT(1)                   NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- service_booking
-- =========================
CREATE TABLE `service_booking`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_id`   BIGINT UNSIGNED NOT NULL,
    `service_id`       BIGINT UNSIGNED NOT NULL,
    `quantity`         INT             NOT NULL DEFAULT 1,
    `status`           VARCHAR(50)     NOT NULL COMMENT 'Service booking status: PENDING | CONFIRMED | IN_PROGRESS | FINISHED | CANCELLED',
    `price_at_booking` DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`        TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_service_booking_reservation_id` (`reservation_id`),
    KEY `idx_service_booking_service_id` (`service_id`),
    CONSTRAINT `fk_service_booking_service`
        FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_service_booking_reservation`
        FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- shift
-- =========================
CREATE TABLE `shift`
(
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `shift_name` VARCHAR(100)    NOT NULL,
    `start_time` TIME            NOT NULL,
    `end_time`   TIME            NOT NULL,
    `is_active`  TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- work_schedule
-- =========================
CREATE TABLE `work_schedule`
(
    `id`        BIGINT UNSIGNED                           NOT NULL AUTO_INCREMENT,
    `staff_id`  BIGINT UNSIGNED                           NOT NULL,
    `shift_id`  BIGINT UNSIGNED                           NOT NULL,
    `work_date` DATE                                      NOT NULL,
    `status`    ENUM ('SCHEDULED','ON_LEAVE','COMPLETED') NOT NULL DEFAULT 'SCHEDULED' COMMENT 'Work schedule status ENUM: SCHEDULED | ON_LEAVE | COMPLETED',
    `is_active` TINYINT(1)                                NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_work_schedule_staff_id` (`staff_id`),
    KEY `idx_work_schedule_shift_id` (`shift_id`),
    CONSTRAINT `fk_work_schedule_staff`
        FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_work_schedule_shift`
        FOREIGN KEY (`shift_id`) REFERENCES `shift` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- folio
-- =========================
CREATE TABLE `folio`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_id` BIGINT UNSIGNED NOT NULL,
    `total_charges`  DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `total_paid`     DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `balance`        DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `status`         VARCHAR(50)     NOT NULL COMMENT 'Folio status: OPEN | LOCKED | SETTLED',
    `is_active`      TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_folio_reservation_id` (`reservation_id`),
    CONSTRAINT `fk_folio_reservation`
        FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- folio_item
-- =========================
CREATE TABLE `folio_item`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `folio_id`           BIGINT UNSIGNED NOT NULL,
    `type`               VARCHAR(50)     NOT NULL COMMENT 'Folio item type: ROOM_CHARGE | SERVICE_CHARGE | EARLY_CHECKIN_FEE | LATE_CHECKOUT_FEE | DAMAGE_PENALTY | ADJUSTMENT | DISCOUNT | REFUND',
    `service_booking_id` BIGINT UNSIGNED NULL,
    `description`        TEXT            NULL,
    `quantity`           INT             NOT NULL DEFAULT 1,
    `total_price`        DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `status`             VARCHAR(50)     NOT NULL COMMENT 'Folio item status: UNPAID | PAID | VOID',
    `is_active`          TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_folio_item_folio_id` (`folio_id`),
    KEY `idx_folio_item_service_booking_id` (`service_booking_id`),
    CONSTRAINT `fk_folio_item_folio`
        FOREIGN KEY (`folio_id`) REFERENCES `folio` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_folio_item_service_booking`
        FOREIGN KEY (`service_booking_id`) REFERENCES `service_booking` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- payment_transaction
-- =========================
CREATE TABLE `payment_transaction`
(
    `id`                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `folio_id`              BIGINT UNSIGNED NOT NULL,
    `code`                  VARCHAR(50)     NOT NULL,
    `transaction_reference` VARCHAR(255)    NULL,
    `payment_method`        VARCHAR(50)     NOT NULL COMMENT 'Payment method: CASH | CARD | BANK_TRANSFER | QR | VNPAY',
    `amount`                DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `type`                  VARCHAR(50)     NOT NULL COMMENT 'Payment transaction type: DEPOSIT | PAYMENT | REFUND | ADJUSTMENT',
    `status`                VARCHAR(50)     NOT NULL COMMENT 'Payment transaction status: PENDING | SUCCESS | FAILED | CANCELLED',
    `created_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `handled_by`            BIGINT UNSIGNED NULL COMMENT 'FK -> staff.id (who handled/recorded this transaction)',
    `is_active`             TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_transaction_code` (`code`),
    KEY `idx_payment_transaction_folio_id` (`folio_id`),
    KEY `idx_payment_transaction_handled_by` (`handled_by`),
    CONSTRAINT `fk_payment_transaction_folio`
        FOREIGN KEY (`folio_id`) REFERENCES `folio` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_payment_transaction_handled_by_staff`
        FOREIGN KEY (`handled_by`) REFERENCES `staff` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- payment_allocation
-- =========================
CREATE TABLE `payment_allocation`
(
    `id`                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `payment_transaction_id` BIGINT UNSIGNED NOT NULL,
    `folio_item_id`          BIGINT UNSIGNED NOT NULL,
    `amount_applied`         DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`              TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_payment_allocation_payment_transaction_id` (`payment_transaction_id`),
    KEY `idx_payment_allocation_folio_item_id` (`folio_item_id`),
    CONSTRAINT `fk_payment_allocation_payment_transaction`
        FOREIGN KEY (`payment_transaction_id`) REFERENCES `payment_transaction` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_payment_allocation_folio_item`
        FOREIGN KEY (`folio_item_id`) REFERENCES `folio_item` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- rating
-- =========================
CREATE TABLE `rating`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_id` BIGINT UNSIGNED NOT NULL,
    `customer_id`    BIGINT UNSIGNED NOT NULL,
    `rating`         INT             NOT NULL,
    `comment`        TEXT            NULL,
    `review_date`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_public`      TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Public flag: 1=public, 0=private',
    `is_active`      TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_rating_reservation_id` (`reservation_id`),
    KEY `idx_rating_customer_id` (`customer_id`),
    CONSTRAINT `fk_rating_reservation`
        FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_rating_customer`
        FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- asset_category
-- =========================
CREATE TABLE `asset_category`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255)    NOT NULL,
    `description` TEXT            NULL,
    `is_active`   TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- asset
-- =========================
CREATE TABLE `asset`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `category_id`        BIGINT UNSIGNED NOT NULL,
    `name`               VARCHAR(255)    NOT NULL,
    `total_quantity`     INT             NOT NULL DEFAULT 0,
    `available_quantity` INT             NOT NULL DEFAULT 0,
    `price`              DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`          TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_asset_category_id` (`category_id`),
    CONSTRAINT `fk_asset_category`
        FOREIGN KEY (`category_id`) REFERENCES `asset_category` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- room_asset
-- =========================
CREATE TABLE `room_asset`
(
    `id`        BIGINT UNSIGNED         NOT NULL AUTO_INCREMENT,
    `room_id`   BIGINT UNSIGNED         NOT NULL,
    `asset_id`  BIGINT UNSIGNED         NOT NULL,
    `quantity`  INT                     NOT NULL DEFAULT 0,
    `status`    ENUM ('Good','Damaged') NOT NULL DEFAULT 'Good' COMMENT 'Room asset status ENUM: Good | Damaged',
    `is_active` TINYINT(1)              NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_room_asset_room_id` (`room_id`),
    KEY `idx_room_asset_asset_id` (`asset_id`),
    CONSTRAINT `fk_room_asset_room`
        FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_room_asset_asset`
        FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- damage_report
-- =========================
CREATE TABLE `damage_report`
(
    `id`                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_id`              BIGINT UNSIGNED NOT NULL,
    `reported_by_staff_id` BIGINT UNSIGNED NOT NULL,
    `reservation_id`       BIGINT UNSIGNED NULL,
    `quantity`             INT             NOT NULL DEFAULT 1,
    `penalty_amount`       DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `status`               VARCHAR(50)     NOT NULL COMMENT 'Damage report status (suggested): OPEN | RESOLVED | CANCELLED',
    `is_active`            TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_damage_report_room_id` (`room_id`),
    KEY `idx_damage_report_reported_by_staff_id` (`reported_by_staff_id`),
    KEY `idx_damage_report_reservation_id` (`reservation_id`),
    CONSTRAINT `fk_damage_report_room`
        FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_damage_report_reservation`
        FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT `fk_damage_report_staff`
        FOREIGN KEY (`reported_by_staff_id`) REFERENCES `staff` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- room_img
-- =========================
CREATE TABLE `room_img`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_class_id` BIGINT UNSIGNED NOT NULL,
    `img_url`       VARCHAR(2048)   NOT NULL,
    `img_type`      VARCHAR(50)     NULL COMMENT 'Image type (suggested): THUMBNAIL | GALLERY | FLOORPLAN',
    `is_primary`    TINYINT(1)      NOT NULL DEFAULT 0 COMMENT 'Primary image flag: 1=yes, 0=no',
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_room_img_room_class_id` (`room_class_id`),
    CONSTRAINT `fk_room_img_room_class`
        FOREIGN KEY (`room_class_id`) REFERENCES `room_class` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- housekeeping_task
-- =========================
CREATE TABLE `housekeeping_task`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_id`      BIGINT UNSIGNED NOT NULL,
    `assignee_id`  BIGINT UNSIGNED NULL,
    `task_type`    VARCHAR(50)     NOT NULL COMMENT 'Task type (suggested): CLEANING | INSPECTION | MAINTENANCE_SUPPORT',
    `status`       VARCHAR(50)     NOT NULL COMMENT 'Housekeeping status (suggested): SCHEDULED | IN_PROGRESS | COMPLETED | CANCELLED',
    `assigned_at`  DATETIME        NULL,
    `completed_at` DATETIME        NULL,
    `is_active`    TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_housekeeping_task_room_id` (`room_id`),
    KEY `idx_housekeeping_task_assignee_id` (`assignee_id`),
    CONSTRAINT `fk_housekeeping_task_room`
        FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_housekeeping_task_assignee`
        FOREIGN KEY (`assignee_id`) REFERENCES `staff` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- asset_handover
-- =========================
CREATE TABLE `asset_handover`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `staff_id`      BIGINT UNSIGNED NOT NULL,
    `asset_id`      BIGINT UNSIGNED NOT NULL,
    `quantity`      INT             NOT NULL DEFAULT 0,
    `handover_date` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_asset_handover_staff_id` (`staff_id`),
    KEY `idx_asset_handover_asset_id` (`asset_id`),
    CONSTRAINT `fk_asset_handover_staff`
        FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_asset_handover_asset`
        FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =========================
-- refund_request
-- =========================
CREATE TABLE `refund_request`
(
    `id`                     BIGINT UNSIGNED                                 NOT NULL AUTO_INCREMENT,
    `payment_transaction_id` BIGINT UNSIGNED                                 NOT NULL,
    `amount`                 DECIMAL(12, 2)                                  NOT NULL DEFAULT 0,
    `reason`                 TEXT                                            NULL,
    `reject_reason`          TEXT                                            NULL,
    `status`                 ENUM ('PENDING','APPROVED','REJECTED','FAILED') NOT NULL DEFAULT 'PENDING'
        COMMENT 'Refund request status ENUM: PENDING | APPROVED | REJECTED | FAILED',
    `requested_by`           BIGINT UNSIGNED                                 NOT NULL,
    `approved_by`            BIGINT UNSIGNED                                 NULL,
    `created_at`             DATETIME                                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`             DATETIME                                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_active`              TINYINT(1)                                      NOT NULL DEFAULT 1 COMMENT 'Soft delete flag: 1=active, 0=inactive',
    PRIMARY KEY (`id`),
    KEY `idx_refund_request_payment_transaction_id` (`payment_transaction_id`),
    KEY `idx_refund_request_requested_by` (`requested_by`),
    KEY `idx_refund_request_approved_by` (`approved_by`),
    CONSTRAINT `fk_refund_request_payment_transaction`
        FOREIGN KEY (`payment_transaction_id`) REFERENCES `payment_transaction` (`id`)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_refund_request_requested_by_staff`
        FOREIGN KEY (`requested_by`) REFERENCES `staff` (`id`)
            ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_refund_request_approved_by_staff`
        FOREIGN KEY (`approved_by`) REFERENCES `staff` (`id`)
            ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;