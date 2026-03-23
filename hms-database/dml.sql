-- =========================================================
-- HMS DB - DML Data Generation Script (50-100 records)
-- =========================================================

USE `hms_db`;

SET AUTOCOMMIT = 0;
START TRANSACTION;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `refund_request`;
TRUNCATE TABLE `asset_handover`;
TRUNCATE TABLE `housekeeping_task`;
TRUNCATE TABLE `room_img`;
TRUNCATE TABLE `damage_report`;
TRUNCATE TABLE `room_asset`;
TRUNCATE TABLE `asset`;
TRUNCATE TABLE `asset_category`;
TRUNCATE TABLE `rating`;
TRUNCATE TABLE `payment_allocation`;
TRUNCATE TABLE `payment_transaction`;
TRUNCATE TABLE `folio_item`;
TRUNCATE TABLE `folio`;
TRUNCATE TABLE `work_schedule`;
TRUNCATE TABLE `shift`;
TRUNCATE TABLE `staff`;
TRUNCATE TABLE `customer`;
TRUNCATE TABLE `user`;
TRUNCATE TABLE `service_booking`;
TRUNCATE TABLE `service`;
TRUNCATE TABLE `room_occupant`;
TRUNCATE TABLE `reservation_room`;
TRUNCATE TABLE `reservation`;
TRUNCATE TABLE `room`;
TRUNCATE TABLE `room_class`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. ROOM CLASS (3 bản ghi)
INSERT INTO `room_class` (`id`, `name`, `base_price`, `standard_capacity`, `max_capacity`, `extra_person_fee`)
VALUES (1, 'Standard', 800000, 2, 3, 150000),
       (2, 'Deluxe', 1200000, 2, 4, 200000),
       (3, 'Suite', 2500000, 2, 5, 300000);

-- 2. ROOM (Tạo 60 phòng: 101-120, 201-220, 301-320)
INSERT INTO `room` (`room_number`, `room_class_id`, `status`, `is_active`)
SELECT CONCAT(floor, LPAD(num, 2, '0')),
       floor, -- floor 1=Std, 2=Dlx, 3=Suite
       'AVAILABLE',
       1
FROM (SELECT 1 AS floor UNION SELECT 2 UNION SELECT 3) f
         CROSS JOIN (SELECT a.n + b.n * 10 AS num
                     FROM (SELECT 1 n
                           UNION
                           SELECT 2
                           UNION
                           SELECT 3
                           UNION
                           SELECT 4
                           UNION
                           SELECT 5
                           UNION
                           SELECT 6
                           UNION
                           SELECT 7
                           UNION
                           SELECT 8
                           UNION
                           SELECT 9
                           UNION
                           SELECT 10) a,
                          (SELECT 0 n UNION SELECT 1) b) n
WHERE num <= 20;

-- 3. USER (Tạo 110 bản ghi: 10 Staff/Admin + 100 Customer)
-- Password mặc định: password123
INSERT INTO `user` (`id`, `email`, `password`, `role`, `is_active`)
VALUES (1, 'admin@hms.com', '$2a$12$xVAEx8P3Riu5jTn.n5Rk2uadkWFKuMsxiXkX4mzyUyLXP5Tf8TjHG', 'ADMIN', 1);

INSERT INTO `user` (`email`, `password`, `role`)
SELECT CONCAT('staff', n, '@hms.com'), '$2a$12$xVAEx8P3Riu5jTn.n5Rk2uadkWFKuMsxiXkX4mzyUyLXP5Tf8TjHG', 'STAFF'
FROM (SELECT @row := @row + 1 AS n
      FROM (SELECT 0
            UNION
            SELECT 1
            UNION
            SELECT 2
            UNION
            SELECT 3
            UNION
            SELECT 4
            UNION
            SELECT 5
            UNION
            SELECT 6
            UNION
            SELECT 7
            UNION
            SELECT 8
            UNION
            SELECT 9) a,
           (SELECT @row := 1) r) t;

INSERT INTO `user` (`email`, `password`, `role`)
SELECT CONCAT('guest', n, '@gmail.com'), NULL, 'CUSTOMER'
FROM (SELECT @row2 := @row2 + 1 AS n
      FROM (SELECT 0
            UNION
            SELECT 1
            UNION
            SELECT 2
            UNION
            SELECT 3
            UNION
            SELECT 4
            UNION
            SELECT 5
            UNION
            SELECT 6
            UNION
            SELECT 7
            UNION
            SELECT 8
            UNION
            SELECT 9) a,
           (SELECT 0
            UNION
            SELECT 1
            UNION
            SELECT 2
            UNION
            SELECT 3
            UNION
            SELECT 4
            UNION
            SELECT 5
            UNION
            SELECT 6
            UNION
            SELECT 7
            UNION
            SELECT 8
            UNION
            SELECT 9) b,
           (SELECT @row2 := 0) r) t;

-- 4. STAFF (10 bản ghi)
INSERT INTO `staff` (`user_id`, `department`, `full_name`, `phone_number`, `status`)
SELECT id,
       IF(id % 2 = 0, 'RECEPTIONIST', 'HOUSEKEEPING'),
       CONCAT('NV Nguyen ', id),
       CONCAT('0912', LPAD(id, 6, '0')),
       'ACTIVE'
FROM `user`
WHERE `role` = 'STAFF';

-- 5. CUSTOMER (100 bản ghi)
INSERT INTO `customer` (`full_name`, `phone_number`, `identity_card`, `email`, `type`, `user_id`)
SELECT CONCAT('Khách Hàng ', id),
       CONCAT('0908', LPAD(id, 6, '0')),
       CONCAT('0790', LPAD(id, 8, '0')),
       email,
       'ADULT',
       id
FROM `user`
WHERE `role` = 'CUSTOMER';

-- 6. RESERVATION (80 bản ghi)
INSERT INTO `reservation` (`code`, `customer_id`, `expected_check_in`, `expected_check_out`, `status`, `total_deposit`,
                           `created_at`)
SELECT CONCAT('RSV-', 202603, LPAD(n, 4, '0')),
       (SELECT id FROM `customer` ORDER BY RAND() LIMIT 1),
       DATE_ADD('2026-03-01', INTERVAL n DAY),
       DATE_ADD('2026-03-01', INTERVAL n + 2 DAY),
       IF(n < 40, 'FINISHED', 'CONFIRMED'),
       IF(n % 2 = 0, 500000, 0),
       DATE_SUB(NOW(), INTERVAL n HOUR)
FROM (SELECT @r3 := @r3 + 1 AS n
      FROM (SELECT 0
            UNION
            SELECT 1
            UNION
            SELECT 2
            UNION
            SELECT 3
            UNION
            SELECT 4
            UNION
            SELECT 5
            UNION
            SELECT 6
            UNION
            SELECT 7
            UNION
            SELECT 8
            UNION
            SELECT 9) a,
           (SELECT 0
            UNION
            SELECT 1
            UNION
            SELECT 2
            UNION
            SELECT 3
            UNION
            SELECT 4
            UNION
            SELECT 5
            UNION
            SELECT 6
            UNION
            SELECT 7) b,
           (SELECT @r3 := 0) r) t;

-- 7. RESERVATION_ROOM (1:1 với Reservation để đơn giản hóa mẫu)
INSERT INTO `reservation_room` (`reservation_id`, `room_class_id`, `room_id`, `status`, `price_at_booking`)
SELECT id,
       (id % 3) + 1,
       (SELECT id FROM `room` WHERE room_class_id = (id % 3) + 1 LIMIT 1 OFFSET 2), -- Offset để tránh trùng lặp đơn giản
       IF(id < 40, 'CHECKED_OUT', 'PENDING'),
       IF((id % 3) + 1 = 1, 800000, IF((id % 3) + 1 = 2, 1200000, 2500000))
FROM `reservation`;

-- 9. SERVICE (10 bản ghi)
INSERT INTO `service` (`name`, `service_category`, `price`)
VALUES ('Buffet sáng', 'F&B', 200000),
       ('Coca Cola', 'Minibar', 40000),
       ('Giặt ủi', 'General', 60000),
       ('Massage body', 'Spa', 500000),
       ('Đưa đón sân bay', 'Transport', 400000),
       ('Mì ly', 'Minibar', 30000),
       ('Rượu vang', 'Minibar', 1200000),
       ('Hơi nước', 'Spa', 150000),
       ('Thuê xe máy', 'Transport', 150000),
       ('Trà đào', 'F&B', 45000);

-- 10. SERVICE BOOKING (100 bản ghi ngẫu nhiên)
INSERT INTO `service_booking` (`reservation_room_id`, `service_id`, `quantity`, `status`, `price_at_booking`)
SELECT (SELECT id FROM `reservation_room` ORDER BY RAND() LIMIT 1),
       (SELECT id FROM `service` ORDER BY RAND() LIMIT 1),
       (t.n % 3) + 1,
       'FINISHED',
       0 -- Sẽ update sau
FROM (SELECT @r4 := @r4 + 1 AS n
      FROM (SELECT 0 - 9) a,
           (SELECT 0 - 9) b,
           (SELECT @r4 := 0) r) t;

UPDATE `service_booking` sb JOIN `service` s ON sb.service_id = s.id
SET sb.price_at_booking = s.price;

-- 11. FOLIO (Mỗi booking có 1 folio)
INSERT INTO `folio` (`reservation_room_id`, `total_charges`, `total_paid`, `balance`, `status`)
SELECT id, 0, 0, 0, IF(status = 'CHECKED_OUT', 'SETTLED', 'OPEN')
FROM `reservation_room`;

-- 12. FOLIO ITEM (Tiền phòng + Tiền dịch vụ)
-- Chèn tiền phòng
INSERT INTO `folio_item` (`folio_id`, `type`, `description`, `quantity`, `total_price`, `status`)
SELECT f.id, 'ROOM_CHARGE', 'Tiền phòng', 2, rr.price_at_booking * 2, IF(f.status = 'SETTLED', 'PAID', 'UNPAID')
FROM `folio` f
         JOIN `reservation_room` rr ON f.reservation_room_id = rr.id;

-- Chèn tiền dịch vụ từ service_booking
INSERT INTO `folio_item` (`folio_id`, `type`, `service_booking_id`, `description`, `quantity`, `total_price`, `status`)
SELECT f.id,
       'SERVICE_CHARGE',
       sb.id,
       s.name,
       sb.quantity,
       sb.price_at_booking * sb.quantity,
       IF(f.status = 'SETTLED', 'PAID', 'UNPAID')
FROM `service_booking` sb
         JOIN `reservation_room` rr ON sb.reservation_room_id = rr.id
         JOIN `folio` f ON f.reservation_room_id = rr.id
         JOIN `service` s ON sb.service_id = s.id;

-- Cập nhật tổng tiền Folio
UPDATE `folio` f
SET f.total_charges = (SELECT SUM(total_price) FROM `folio_item` WHERE folio_id = f.id),
    f.total_paid    = IF(status = 'SETTLED', (SELECT SUM(total_price) FROM `folio_item` WHERE folio_id = f.id), 0),
    f.balance       = f.total_charges - f.total_paid;

-- 13. RATING (50 bản ghi)
INSERT INTO `rating` (`reservation_id`, `customer_id`, `rating`, `comment`)
SELECT r.id,
       r.customer_id,
       (r.id % 2) + 4,
       'Dịch vụ rất tốt, tôi sẽ quay lại!'
FROM `reservation` r
WHERE r.status = 'FINISHED'
LIMIT 50;

-- 14. ASSET & ROOM ASSET
INSERT INTO `asset_category` (`id`, `name`)
VALUES (1, 'Điện tử'),
       (2, 'Nội thất');
INSERT INTO `asset` (`id`, `category_id`, `name`, `total_quantity`, `available_quantity`, `price`)
VALUES (1, 1, 'Smart TV 43', 100, 60, 7000000),
       (2, 2, 'Giường King', 50, 40, 15000000);

INSERT INTO `room_asset` (`room_id`, `asset_id`, `quantity`, `status`)
SELECT id, 1, 1, 'Good'
FROM `room`
LIMIT 50;

COMMIT;
SET AUTOCOMMIT = 1;

-- Kiểm tra số lượng
SELECT 'room' as table_name, COUNT(*)
FROM room
UNION
SELECT 'user', COUNT(*)
FROM user
UNION
SELECT 'customer', COUNT(*)
FROM customer
UNION
SELECT 'reservation', COUNT(*)
FROM reservation
UNION
SELECT 'folio_item', COUNT(*)
FROM folio_item;

USE `hms_db`;

START TRANSACTION;

-- =========================================================
-- CẬP NHẬT CÁC CASE ĐANG Ở (IN-HOUSE / OCCUPIED)
-- =========================================================

-- 1. Cập nhật trạng thái Reservation sang 'IN_HOUSE' cho các ID từ 41 đến 65
UPDATE `reservation`
SET `status`             = 'IN_HOUSE',
    `expected_check_in`  = DATE_SUB(NOW(), INTERVAL 1 DAY),
    `expected_check_out` = DATE_ADD(NOW(), INTERVAL 1 DAY)
WHERE `id` BETWEEN 41 AND 65;

-- 2. Cập nhật Reservation_Room tương ứng sang 'CHECKED_IN'
-- Đồng thời gán ngày check-in thực tế
UPDATE `reservation_room`
SET `status`          = 'CHECKED_IN',
    `actual_check_in` = DATE_SUB(NOW(), INTERVAL 1 DAY)
WHERE `reservation_id` BETWEEN 41 AND 65;

-- 3. Cập nhật trạng thái phòng (ROOM) sang 'OCCUPIED' 
-- dựa trên các phòng đang được khách check-in
UPDATE `room` r
    JOIN `reservation_room` rr ON r.`id` = rr.`room_id`
SET r.`status` = 'OCCUPIED'
WHERE rr.`status` = 'CHECKED_IN';

-- 4. Tạo dữ liệu người ở thực tế (ROOM_OCCUPANT) cho các case đang ở
-- Giả sử mỗi phòng đang ở có ít nhất 1 khách chính
INSERT INTO `room_occupant` (`reservation_room_id`, `customer_id`, `role`)
SELECT rr.id,
       res.customer_id,
       'PRIMARY'
FROM `reservation_room` rr
         JOIN `reservation` res ON rr.reservation_id = res.id
WHERE rr.status = 'CHECKED_IN';

-- 5. Cập nhật một số phòng sang trạng thái khác để dữ liệu đa dạng
-- Một số phòng vừa trả khách xong chưa dọn (DIRTY)
UPDATE `room`
SET `status` = 'DIRTY'
WHERE `id` IN (5, 10, 15, 20);

-- Một số phòng đang bảo trì (MAINTENANCE)
UPDATE `room`
SET `status` = 'MAINTENANCE'
WHERE `id` IN (25, 30);

-- 6. Tạo Task dọn phòng cho các phòng DIRTY
INSERT INTO `housekeeping_task` (`room_id`, `assignee_id`, `task_type`, `status`, `assigned_at`)
SELECT id,
       (SELECT id FROM `staff` WHERE department = 'HOUSEKEEPING' LIMIT 1),
       'CLEANING',
       'SCHEDULED',
       NOW()
FROM `room`
WHERE `status` = 'DIRTY';

-- 7. Cập nhật Folio cho khách đang ở (Balance thường sẽ > 0 vì chưa thanh toán hết)
UPDATE `folio` f
    JOIN `reservation_room` rr ON f.reservation_room_id = rr.id
SET f.status     = 'OPEN',
    f.total_paid = (f.total_charges * 0.3), -- Khách mới cọc hoặc trả trước 1 phần
    f.balance    = f.total_charges - (f.total_charges * 0.3)
WHERE rr.status = 'CHECKED_IN';

COMMIT;

-- =========================================================
-- KIỂM TRA TRẠNG THÁI SAU KHI CẬP NHẬT
-- =========================================================
SELECT 'Trạng thái phòng' as info, status, COUNT(*)
FROM room
GROUP BY status
UNION
SELECT 'Trạng thái đặt phòng', status, COUNT(*)
FROM reservation
GROUP BY status;