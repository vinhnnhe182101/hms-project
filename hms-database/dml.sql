-- MySQL 8 DML seed data (snake_case) for hms_db
-- Updated to match latest schema changes:
-- - reservation_room_allocation -> reservation_room
-- - service_booking now references reservation_room via reservation_room_id
-- - folio now references reservation_room via reservation_room_id (1 folio per reservation_room)
-- - room_occupant: allocation_id -> reservation_room_id
--
-- Safe re-run strategy: TRUNCATE in FK order with FOREIGN_KEY_CHECKS=0

USE `hms_db`;

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

-- 1) ROOM CLASS
INSERT INTO `room_class`
(`id`, `name`, `base_price`, `standard_capacity`, `max_capacity`, `extra_person_fee`, `is_active`)
VALUES (1, 'Standard', 800000.00, 2, 3, 150000.00, 1),
       (2, 'Deluxe', 1200000.00, 2, 4, 200000.00, 1),
       (3, 'Suite', 2500000.00, 2, 5, 300000.00, 1);

-- 2) ROOM
INSERT INTO `room`
(`id`, `room_number`, `room_class_id`, `status`, `description`, `is_active`)
VALUES (1, '101', 1, 'AVAILABLE', 'Standard room - floor 1', 1),
       (2, '102', 1, 'AVAILABLE', 'Standard room - floor 1', 1),
       (3, '201', 2, 'AVAILABLE', 'Deluxe room - floor 2', 1),
       (4, '202', 2, 'MAINTENANCE', 'Deluxe room - maintenance', 1),
       (5, '301', 3, 'AVAILABLE', 'Suite - floor 3', 1);

-- 3) USER
INSERT INTO `user`
(`id`, `email`, `password`, `role`, `provider`, `provider_id`, `is_active`)
VALUES (1, 'admin@hotel.local', '$2y$10$dummyhashadmin', 'ADMIN', 'local', NULL, 1),
       (2, 'reception@hotel.local', '$2y$10$dummyhashreception', 'RECEPTIONIST', 'local', NULL, 1),
       (3, 'housekeeper@hotel.local', '$2y$10$dummyhashhk', 'HOUSEKEEPING', 'local', NULL, 1),
       (4, 'john.customer@gmail.com', NULL, 'CUSTOMER', 'google', 'google-oauth2|john123', 1),
       (5, 'anna.customer@hotel.local', '$2y$10$dummyhashanna', 'CUSTOMER', 'local', NULL, 1);

-- 4) STAFF
INSERT INTO `staff`
(`id`, `user_id`, `department`, `full_name`, `phone_number`, `status`, `is_active`)
VALUES (1, 2, 'Front Office', 'Nguyễn Lễ', '0901000001', 'ACTIVE', 1),
       (2, 3, 'Housekeeping', 'Trần My', '0901000002', 'ACTIVE', 1),
       (3, 1, 'Management', 'Phạm Admin', '0901000003', 'ACTIVE', 1);

-- 5) CUSTOMER
INSERT INTO `customer`
(`id`, `full_name`, `phone_number`, `identity_card`, `email`, `type`, `guardian_id`, `user_id`, `is_active`)
VALUES (1, 'John Nguyen', '0902000001', '012345678901', 'john.customer@gmail.com', 'ADULT', NULL, 4, 1),
       (2, 'Anna Tran', '0902000002', '012345678902', 'anna.customer@hotel.local', 'ADULT', NULL, 5, 1),
       (3, 'Be Nguyen', '0902000003', NULL, NULL, 'CHILD', 1, NULL, 1),
       (4, 'Guest Friend', '0902000004', '012345678904', NULL, 'ADULT', NULL, NULL, 1);

-- 6) RESERVATION (booking header)
INSERT INTO `reservation`
(`id`, `code`, `customer_id`, `expected_check_in`, `expected_check_out`, `status`,
 `total_deposit`, `number_of_members`, `note`, `created_at`, `is_active`)
VALUES (1, 'RSV-20260301-0001', 1, '2026-03-10 14:00:00', '2026-03-12 12:00:00', 'PENDING_DEPOSIT',
        500000.00, 3, 'Gia đình đi du lịch', '2026-03-01 10:00:00', 1),
       (2, 'RSV-20260301-0002', 2, '2026-03-15 14:00:00', '2026-03-16 12:00:00', 'PENDING_DEPOSIT',
        0.00, 1, NULL, '2026-03-01 11:00:00', 1);

-- 7) RESERVATION ROOM (per room slot)
INSERT INTO `reservation_room`
(`id`, `reservation_id`, `room_class_id`, `room_id`, `status`, `number_of_people`, `price_at_booking`,
 `actual_check_out`, `is_active`)
VALUES (1, 1, 2, 3, 'ASSIGNED', 3, 1200000.00, NULL, 1), -- Deluxe 201: John + child + 1 guest total 3 people
       (2, 2, 1, 1, 'ASSIGNED', 1, 800000.00, NULL, 1);
-- Standard 101: Anna 1 person

-- 8) ROOM OCCUPANT (attached to reservation_room)
INSERT INTO `room_occupant`
    (`id`, `reservation_room_id`, `customer_id`, `role`, `is_active`)
VALUES (1, 1, 1, 'PRIMARY', 1),
       (2, 1, 3, 'CHILD', 1),
       (3, 1, 4, 'GUEST', 1),
       (4, 2, 2, 'PRIMARY', 1);

-- 9) SERVICE CATALOG
INSERT INTO `service`
    (`id`, `name`, `service_category`, `price`, `is_active`)
VALUES (1, 'Breakfast Buffet', 'F&B', 200000.00, 1),
       (2, 'Minibar - Soft Drink', 'Minibar', 50000.00, 1),
       (3, 'Aroma Massage 60min', 'Spa', 700000.00, 1);

-- 10) SERVICE BOOKING (links to reservation_room)
INSERT INTO `service_booking`
(`id`, `reservation_room_id`, `service_id`, `quantity`, `status`, `price_at_booking`, `is_active`)
VALUES (1, 1, 1, 3, 'PENDING', 200000.00, 1), -- Breakfast for reservation_room 1
       (2, 1, 3, 1, 'PENDING', 700000.00, 1), -- Spa for reservation_room 1
       (3, 2, 2, 2, 'PENDING', 50000.00, 1);
-- Minibar for reservation_room 2

-- 11) FOLIO (1 folio per reservation_room)
INSERT INTO `folio`
(`id`, `reservation_room_id`, `total_charges`, `total_paid`, `balance`, `status`, `is_active`)
VALUES (1, 1, 0.00, 0.00, 0.00, 'OPEN', 1),
       (2, 2, 0.00, 0.00, 0.00, 'OPEN', 1);

-- 12) FOLIO ITEM
-- reservation_room 1: 2 nights Deluxe + services
-- reservation_room 2: 1 night Standard + minibar
INSERT INTO `folio_item`
(`id`, `folio_id`, `type`, `service_booking_id`, `description`, `quantity`, `total_price`, `status`, `is_active`)
VALUES (1, 1, 'ROOM_CHARGE', NULL, 'Room charge (Deluxe) - 2 nights', 2, 2400000.00, 'UNPAID', 1),
       (2, 1, 'SERVICE_CHARGE', 1, 'Breakfast Buffet', 3, 600000.00, 'UNPAID', 1),
       (3, 1, 'SERVICE_CHARGE', 2, 'Aroma Massage 60min', 1, 700000.00, 'UNPAID', 1),
       (4, 2, 'ROOM_CHARGE', NULL, 'Room charge (Standard) - 1 night', 1, 800000.00, 'UNPAID', 1),
       (5, 2, 'SERVICE_CHARGE', 3, 'Minibar - Soft Drink', 2, 100000.00, 'UNPAID', 1);

-- Update folio totals
UPDATE `folio` f
SET f.`total_charges` = (SELECT IFNULL(SUM(fi.`total_price`), 0)
                         FROM `folio_item` fi
                         WHERE fi.`folio_id` = f.`id`),
    f.`balance`       = f.`total_charges` - f.`total_paid`
WHERE f.`id` IN (1, 2);

-- 13) PAYMENT TRANSACTION
-- Deposit for reservation_room 1 + final payment for reservation_room 1
INSERT INTO `payment_transaction`
(`id`, `folio_id`, `code`, `transaction_reference`, `payment_method`, `amount`,
 `type`, `status`, `created_at`, `handled_by`, `is_active`)
VALUES (1, 1, 'PAY-20260301-0001', 'VNPAY-REF-AAA001', 'VNPAY', 500000.00,
        'DEPOSIT', 'SUCCESS', '2026-03-01 10:05:00', 1, 1),
       (2, 1, 'PAY-20260312-0001', 'CASH-REC-0001', 'CASH', 3200000.00,
        'PAYMENT', 'SUCCESS', '2026-03-12 11:00:00', 1, 1);

-- 14) PAYMENT ALLOCATION
INSERT INTO `payment_allocation`
(`id`, `payment_transaction_id`, `folio_item_id`, `amount_applied`, `is_active`)
VALUES (1, 1, 1, 500000.00, 1),  -- deposit applied to room charge
       (2, 2, 1, 1900000.00, 1), -- remaining room charge
       (3, 2, 2, 600000.00, 1),  -- breakfast
       (4, 2, 3, 700000.00, 1);
-- spa

-- Mark paid items if fully covered
UPDATE `folio_item` fi
    JOIN (SELECT `folio_item_id`, SUM(`amount_applied`) AS applied
          FROM `payment_allocation`
          GROUP BY `folio_item_id`) x ON x.`folio_item_id` = fi.`id`
SET fi.`status` = CASE
                      WHEN x.applied >= fi.`total_price` THEN 'PAID'
                      ELSE fi.`status`
    END;

-- Update folio paid/balance
UPDATE `folio` f
    JOIN (SELECT pt.`folio_id` AS folio_id, SUM(pa.`amount_applied`) AS paid
          FROM `payment_allocation` pa
                   JOIN `payment_transaction` pt ON pt.`id` = pa.`payment_transaction_id`
          GROUP BY pt.`folio_id`) x ON x.folio_id = f.`id`
SET f.`total_paid` = x.paid,
    f.`balance`    = f.`total_charges` - x.paid;

-- 15) ASSET + room_asset
INSERT INTO `asset_category`
    (`id`, `name`, `description`, `is_active`)
VALUES (1, 'Electronics', 'TV, hair dryer, etc.', 1),
       (2, 'Furniture', 'Bed, chair, table, etc.', 1),
       (3, 'Linen', 'Towels, sheets, etc.', 1);

INSERT INTO `asset`
(`id`, `category_id`, `name`, `total_quantity`, `available_quantity`, `price`, `is_active`)
VALUES (1, 1, 'TV 42 inch', 10, 8, 6000000.00, 1),
       (2, 1, 'Hair Dryer', 30, 25, 500000.00, 1),
       (3, 3, 'Bath Towel', 200, 180, 120000.00, 1);

INSERT INTO `room_asset`
    (`id`, `room_id`, `asset_id`, `quantity`, `status`, `is_active`)
VALUES (1, 1, 1, 1, 'Good', 1),
       (2, 1, 2, 1, 'Good', 1),
       (3, 3, 1, 1, 'Good', 1),
       (4, 3, 2, 1, 'Good', 1);

-- 16) damage_report
INSERT INTO `damage_report`
(`id`, `room_id`, `reported_by_staff_id`, `reservation_id`, `quantity`, `penalty_amount`, `status`, `is_active`)
VALUES (1, 3, 2, 1, 1, 120000.00, 'OPEN', 1);

-- 17) room_img
INSERT INTO `room_img`
(`id`, `room_class_id`, `img_url`, `img_type`, `is_primary`, `is_active`)
VALUES (1, 1, 'https://cdn.example.com/rooms/standard_1.jpg', 'thumbnail', 1, 1),
       (2, 2, 'https://cdn.example.com/rooms/deluxe_1.jpg', 'thumbnail', 1, 1);

-- 18) shift + work_schedule
INSERT INTO `shift`
    (`id`, `shift_name`, `start_time`, `end_time`, `is_active`)
VALUES (1, 'Morning', '06:00:00', '14:00:00', 1),
       (2, 'Afternoon', '14:00:00', '22:00:00', 1),
       (3, 'Night', '22:00:00', '06:00:00', 1);

INSERT INTO `work_schedule`
    (`id`, `staff_id`, `shift_id`, `work_date`, `status`, `is_active`)
VALUES (1, 1, 2, '2026-03-10', 'SCHEDULED', 1),
       (2, 2, 1, '2026-03-10', 'SCHEDULED', 1),
       (3, 2, 1, '2026-03-12', 'COMPLETED', 1);

-- 19) refund_request
INSERT INTO `refund_request`
(`id`, `payment_transaction_id`, `amount`, `reason`, `reject_reason`, `status`,
 `requested_by`, `approved_by`, `created_at`, `updated_at`, `is_active`)
VALUES (1, 1, 200000.00, 'Khách đổi lịch, hoàn cọc một phần', NULL, 'PENDING',
        1, NULL, '2026-03-02 09:00:00', '2026-03-02 09:00:00', 1);

COMMIT;