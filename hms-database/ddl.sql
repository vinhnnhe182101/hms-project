-- =========================================================
-- HMS DB - MySQL 8 Full Schema (snake_case)
-- Optimized & Formatted
-- =========================================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

DROP DATABASE IF EXISTS `hms_db`;
CREATE DATABASE `hms_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `hms_db`;

-- 1. room_class
CREATE TABLE `room_class`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`              VARCHAR(255)    NOT NULL,
    `base_price`        DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `standard_capacity` INT             NOT NULL DEFAULT 1,
    `max_capacity`      INT             NOT NULL DEFAULT 1,
    `extra_person_fee`  DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`         TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '1=active, 0=inactive',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 2. room
CREATE TABLE `room`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_number`   VARCHAR(50)     NOT NULL,
    `room_class_id` BIGINT UNSIGNED NOT NULL,
    `status`        VARCHAR(50)     NOT NULL COMMENT 'AVAILABLE | RESERVED | CLEAN | DIRTY | OCCUPIED | MAINTENANCE',
    `description`   TEXT            NULL,
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_room_number` (`room_number`),
    KEY `idx_room_room_class_id` (`room_class_id`),
    CONSTRAINT `fk_room_room_class` FOREIGN KEY (`room_class_id`) REFERENCES `room_class` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 3. user
CREATE TABLE `user`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `email`       VARCHAR(255)    NOT NULL,
    `password`    VARCHAR(255)    NULL,
    `role`        VARCHAR(50)     NOT NULL COMMENT 'ADMIN | STAFF | MANAGER | CUSTOMER',
    `provider`    VARCHAR(50)     NOT NULL DEFAULT 'local' COMMENT 'local | google',
    `provider_id` VARCHAR(255)    NULL,
    `is_active`   TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 4. customer
CREATE TABLE `customer`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `full_name`     VARCHAR(255)    NOT NULL,
    `phone_number`  VARCHAR(30)     NULL,
    `identity_card` VARCHAR(50)     NULL,
    `email`         VARCHAR(255)    NULL,
    `type`          VARCHAR(50)     NULL COMMENT 'ADULT | CHILD | VIP | CORPORATE',
    `guardian_id`   BIGINT UNSIGNED NULL,
    `user_id`       BIGINT UNSIGNED NULL,
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_customer_guardian_id` (`guardian_id`),
    KEY `idx_customer_user_id` (`user_id`),
    CONSTRAINT `fk_customer_guardian` FOREIGN KEY (`guardian_id`) REFERENCES `customer` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 5. staff
CREATE TABLE `staff`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT UNSIGNED NULL,
    `department`   VARCHAR(100)    NULL COMMENT 'HOUSEKEEPING | RECEPTIONIST | MANAGEMENT',
    `full_name`    VARCHAR(255)    NOT NULL,
    `phone_number` VARCHAR(30)     NULL,
    `status`       VARCHAR(50)     NOT NULL COMMENT 'ACTIVE | INACTIVE | SUSPENDED',
    `is_active`    TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_staff_user_id` (`user_id`),
    CONSTRAINT `fk_staff_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 6. reservation
CREATE TABLE `reservation`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `code`               VARCHAR(50)     NOT NULL,
    `customer_id`        BIGINT UNSIGNED NOT NULL,
    `expected_check_in`  DATETIME        NOT NULL,
    `expected_check_out` DATETIME        NOT NULL,
    `status`             VARCHAR(50)     NOT NULL COMMENT 'PENDING_DEPOSIT | CONFIRMED | CANCELLED | IN_HOUSE | CHECKED_OUT | FINISHED',
    `total_deposit`      DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `number_of_members`  INT             NOT NULL DEFAULT 1,
    `note`               TEXT            NULL,
    `created_at`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_active`          TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reservation_code` (`code`),
    KEY `idx_reservation_customer_id` (`customer_id`),
    CONSTRAINT `fk_reservation_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 7. reservation_room
CREATE TABLE `reservation_room`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_id`   BIGINT UNSIGNED NOT NULL,
    `room_class_id`    BIGINT UNSIGNED NOT NULL,
    `room_id`          BIGINT UNSIGNED NULL,
    `status`           VARCHAR(50)     NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING | ASSIGNED | CHECKED_IN | CHECKED_OUT | CANCELLED',
    `number_of_people` INT             NOT NULL DEFAULT 1,
    `price_at_booking` DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `actual_check_in`  DATETIME        NULL,
    `actual_check_out` DATETIME        NULL,
    `is_active`        TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_res_room_reservation` (`reservation_id`),
    KEY `idx_res_room_room_class` (`room_class_id`),
    KEY `idx_res_room_room` (`room_id`),
    CONSTRAINT `fk_res_room_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_res_room_room_class` FOREIGN KEY (`room_class_id`) REFERENCES `room_class` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_res_room_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 8. room_occupant
CREATE TABLE `room_occupant`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_room_id` BIGINT UNSIGNED NOT NULL,
    `customer_id`         BIGINT UNSIGNED NOT NULL,
    `role`                VARCHAR(50)     NOT NULL COMMENT 'PRIMARY | GUEST | CHILD',
    `is_active`           TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_room_occupant_res_room` (`reservation_room_id`),
    KEY `idx_room_occupant_customer` (`customer_id`),
    CONSTRAINT `fk_occupant_res_room` FOREIGN KEY (`reservation_room_id`) REFERENCES `reservation_room` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_occupant_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 9. service
CREATE TABLE `service`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(255)    NOT NULL,
    `service_category` VARCHAR(50)     NOT NULL COMMENT 'Spa | Minibar | F&B',
    `price`            DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`        TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 10. service_booking
CREATE TABLE `service_booking`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_room_id` BIGINT UNSIGNED NOT NULL,
    `service_id`          BIGINT UNSIGNED NOT NULL,
    `quantity`            INT             NOT NULL DEFAULT 1,
    `status`              VARCHAR(50)     NOT NULL COMMENT 'PENDING | CONFIRMED | IN_PROGRESS | FINISHED | CANCELLED',
    `price_at_booking`    DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`           TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_sb_reservation_room` (`reservation_room_id`),
    KEY `idx_sb_service` (`service_id`),
    CONSTRAINT `fk_sb_service` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_sb_res_room` FOREIGN KEY (`reservation_room_id`) REFERENCES `reservation_room` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 11. shift
CREATE TABLE `shift`
(
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `shift_name` VARCHAR(100)    NOT NULL,
    `start_time` TIME            NOT NULL,
    `end_time`   TIME            NOT NULL,
    `is_active`  TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 12. work_schedule
CREATE TABLE `work_schedule`
(
    `id`        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `staff_id`  BIGINT UNSIGNED NOT NULL,
    `shift_id`  BIGINT UNSIGNED NOT NULL,
    `work_date` DATE            NOT NULL,
    `status`    VARCHAR(50)     NOT NULL DEFAULT 'SCHEDULED' COMMENT 'SCHEDULED | ON_LEAVE | COMPLETED',
    `is_active` TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_ws_staff` (`staff_id`),
    KEY `idx_ws_shift` (`shift_id`),
    CONSTRAINT `fk_ws_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_ws_shift` FOREIGN KEY (`shift_id`) REFERENCES `shift` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 13. folio
CREATE TABLE `folio`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_room_id` BIGINT UNSIGNED NOT NULL,
    `total_charges`       DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `total_paid`          DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `balance`             DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `status`              VARCHAR(50)     NOT NULL COMMENT 'OPEN | LOCKED | SETTLED',
    `is_active`           TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_folio_res_room` (`reservation_room_id`),
    CONSTRAINT `fk_folio_res_room` FOREIGN KEY (`reservation_room_id`) REFERENCES `reservation_room` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 14. folio_item
CREATE TABLE `folio_item`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `folio_id`           BIGINT UNSIGNED NOT NULL,
    `type`               VARCHAR(50)     NOT NULL COMMENT 'ROOM_CHARGE | SERVICE_CHARGE | EARLY_CHECKIN_FEE | etc.',
    `service_booking_id` BIGINT UNSIGNED NULL,
    `description`        TEXT            NULL,
    `quantity`           INT             NOT NULL DEFAULT 1,
    `total_price`        DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `status`             VARCHAR(50)     NOT NULL COMMENT 'UNPAID | PAID | VOID',
    `is_active`          TINYINT(1)      NOT NULL DEFAULT 1,
    `created_at`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_fi_folio` (`folio_id`),
    KEY `idx_fi_service_booking` (`service_booking_id`),
    CONSTRAINT `fk_fi_folio` FOREIGN KEY (`folio_id`) REFERENCES `folio` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_fi_service_booking` FOREIGN KEY (`service_booking_id`) REFERENCES `service_booking` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 15. payment_transaction
CREATE TABLE `payment_transaction`
(
    `id`                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `folio_id`              BIGINT UNSIGNED NOT NULL,
    `code`                  VARCHAR(50)     NOT NULL,
    `transaction_reference` VARCHAR(255)    NULL,
    `payment_method`        VARCHAR(50)     NOT NULL COMMENT 'CASH | CARD | BANK_TRANSFER | QR | VNPAY',
    `amount`                DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `type`                  VARCHAR(50)     NOT NULL COMMENT 'DEPOSIT | PAYMENT | REFUND | ADJUSTMENT',
    `status`                VARCHAR(50)     NOT NULL COMMENT 'PENDING | SUCCESS | FAILED | CANCELLED',
    `created_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `handled_by`            BIGINT UNSIGNED NULL,
    `is_active`             TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pt_code` (`code`),
    KEY `idx_pt_folio` (`folio_id`),
    KEY `idx_pt_staff` (`handled_by`),
    CONSTRAINT `fk_pt_folio` FOREIGN KEY (`folio_id`) REFERENCES `folio` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_pt_staff` FOREIGN KEY (`handled_by`) REFERENCES `staff` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 16. payment_allocation
CREATE TABLE `payment_allocation`
(
    `id`                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `payment_transaction_id` BIGINT UNSIGNED NOT NULL,
    `folio_item_id`          BIGINT UNSIGNED NOT NULL,
    `amount_applied`         DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`              TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_pa_transaction` (`payment_transaction_id`),
    KEY `idx_pa_folio_item` (`folio_item_id`),
    CONSTRAINT `fk_pa_transaction` FOREIGN KEY (`payment_transaction_id`) REFERENCES `payment_transaction` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_pa_folio_item` FOREIGN KEY (`folio_item_id`) REFERENCES `folio_item` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 17. vnpay_transaction_detail
CREATE TABLE `vnpay_transaction_detail`
(
    `id`                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `payment_transaction_id` BIGINT UNSIGNED NOT NULL,
    `vnp_txn_ref`            VARCHAR(50)     NOT NULL COMMENT 'Mã tham chiếu gửi sang VNPAY',
    `vnp_transaction_no`     VARCHAR(50)     NULL COMMENT 'Mã giao dịch từ VNPAY',
    `vnp_bank_code`          VARCHAR(50)     NULL,
    `vnp_pay_date`           VARCHAR(14)     NULL,
    `raw_response`           TEXT            NULL,
    `is_active`              TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_vnpay_pt` (`payment_transaction_id`),
    CONSTRAINT `fk_vnpay_pt` FOREIGN KEY (`payment_transaction_id`) REFERENCES `payment_transaction` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 18. rating
CREATE TABLE `rating`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_id` BIGINT UNSIGNED NOT NULL,
    `customer_id`    BIGINT UNSIGNED NOT NULL,
    `rating`         INT             NOT NULL,
    `comment`        TEXT            NULL,
    `review_date`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_public`      TINYINT(1)      NOT NULL DEFAULT 1,
    `is_active`      TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_rating_res` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_rating_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 19. asset_category
CREATE TABLE `asset_category`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255)    NOT NULL,
    `description` TEXT            NULL,
    `is_active`   TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 20. asset
CREATE TABLE `asset`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `category_id`        BIGINT UNSIGNED NOT NULL,
    `name`               VARCHAR(255)    NOT NULL,
    `total_quantity`     INT             NOT NULL DEFAULT 0,
    `available_quantity` INT             NOT NULL DEFAULT 0,
    `price`              DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `is_active`          TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_asset_category` FOREIGN KEY (`category_id`) REFERENCES `asset_category` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 21. room_asset
CREATE TABLE `room_asset`
(
    `id`        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_id`   BIGINT UNSIGNED NOT NULL,
    `asset_id`  BIGINT UNSIGNED NOT NULL,
    `quantity`  INT             NOT NULL DEFAULT 0,
    `status`    VARCHAR(50)     NOT NULL DEFAULT 'Good' COMMENT 'Good | Damaged',
    `is_active` TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ra_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_ra_asset` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 22. damage_report
CREATE TABLE `damage_report`
(
    `id`                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_id`              BIGINT UNSIGNED NOT NULL,
    `reported_by_staff_id` BIGINT UNSIGNED NOT NULL,
    `reservation_id`       BIGINT UNSIGNED NULL,
    `quantity`             INT             NOT NULL DEFAULT 1,
    `penalty_amount`       DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `status`               VARCHAR(50)     NOT NULL COMMENT 'OPEN | RESOLVED | CANCELLED',
    `is_active`            TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_dr_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_dr_staff` FOREIGN KEY (`reported_by_staff_id`) REFERENCES `staff` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_dr_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 23. room_img
CREATE TABLE `room_img`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_class_id` BIGINT UNSIGNED NOT NULL,
    `img_url`       VARCHAR(2048)   NOT NULL,
    `img_type`      VARCHAR(50)     NULL COMMENT 'THUMBNAIL | GALLERY | FLOORPLAN',
    `is_primary`    TINYINT(1)      NOT NULL DEFAULT 0,
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_img_room_class` FOREIGN KEY (`room_class_id`) REFERENCES `room_class` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 24. housekeeping_task
CREATE TABLE `housekeeping_task`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `room_id`      BIGINT UNSIGNED NOT NULL,
    `assignee_id`  BIGINT UNSIGNED NULL,
    `task_type`    VARCHAR(50)     NOT NULL COMMENT 'CLEANING | INSPECTION | MAINTENANCE',
    `status`       VARCHAR(50)     NOT NULL COMMENT 'SCHEDULED | IN_PROGRESS | COMPLETED | CANCELLED',
    `assigned_at`  DATETIME        NULL,
    `completed_at` DATETIME        NULL,
    `is_active`    TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_hk_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_hk_staff` FOREIGN KEY (`assignee_id`) REFERENCES `staff` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 25. asset_handover
CREATE TABLE `asset_handover`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `staff_id`      BIGINT UNSIGNED NOT NULL,
    `asset_id`      BIGINT UNSIGNED NOT NULL,
    `quantity`      INT             NOT NULL DEFAULT 0,
    `handover_date` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_active`     TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ho_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_ho_asset` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 26. refund_request
CREATE TABLE `refund_request`
(
    `id`                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `payment_transaction_id` BIGINT UNSIGNED NOT NULL,
    `amount`                 DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    `reason`                 TEXT            NULL,
    `reject_reason`          TEXT            NULL,
    `status`                 VARCHAR(50)     NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING | APPROVED | REJECTED | FAILED',
    `requested_by`           BIGINT UNSIGNED NOT NULL,
    `approved_by`            BIGINT UNSIGNED NULL,
    `created_at`             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_active`              TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_refund_pt` FOREIGN KEY (`payment_transaction_id`) REFERENCES `payment_transaction` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_refund_staff_req` FOREIGN KEY (`requested_by`) REFERENCES `staff` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_refund_staff_app` FOREIGN KEY (`approved_by`) REFERENCES `staff` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;