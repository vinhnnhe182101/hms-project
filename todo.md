# 🗺️ PROJECT ROADMAP: Hotel Management System

## 🎯 Main Goal

- [ ] Hoàn thành một hệ thống quản lý khách sạn cho vai trò lễ tân, bao gồm các chức năng như đặt phòng,
  check-in/check-out, và quản lý dịch vụ.

## 🚀 Active Tasks (Current Sprint)

> Những việc cần ưu tiên xử lý ngay. AI nên tập trung vào phần này.

- (Hiện tại không còn task nào đang mở trong sprint — tất cả các mục bên dưới đã hoàn thành và được chuyển xuống phần ✅
  Done.)

## 📌 Temporary Notes

> Những ghi chú tạm thời đợi xử lý trong sprint này

- **@Valid Annotation**: Hiện tại chưa thêm `@Valid` cho request body validation (tránh rườm rà). Sau khi hoàn thành các
  tính năng core, sẽ thêm validation toàn diện với `@Valid` và custom validators nếu cần.

## 🗂️ Backlog

> Những tính năng hoặc cải tiến sẽ làm sau khi xong Active Tasks.

---

## ✅ Done (Tóm tắt các nhiệm vụ đã hoàn thành)

### A) Done (đã hoàn thành trong sprint hiện tại)

- [x] Chuyển các logic liên quan đến đặt phòng từ ReservationService sang RoomAllocationService, FolioService, ... để
  giảm độ phức tạp của ReservationService.
- [x] Chuyển các logic liên quan đến mapper từ ReservationService sang converter riêng biệt.

- [x] Cập nhật đặt phòng:
    - [x] Chỉ cho phép cập nhật Check-in/check-out date, số lượng người, hạng phòng, người đặt, ghi chú.
    - [x] Không cho phép cập nhật khi trước giờ check-in 24h.
    - [x] Cập nhật lại logic tính toán tiền cọc, sửa đổi Folio và Payment tương ứng.

- [x] Thêm tính năng hủy đặt phòng:
    - [x] Chỉ được hoàn tiền nếu hủy trước giờ check-in 24h.
    - [x] Cập nhật lại Folio và Payment tương ứng (tính phí hủy nếu có).
    - [x] Cập nhật trạng thái đặt phòng thành CANCELLED.

- [x] Thêm tính năng đặt dịch vụ:
    - [x] Chỉ cho phép đặt dịch vụ khi trạng thái đặt phòng là CHECKED_IN. Cho phép sửa số lượng.
    - [x] Cập nhật Folio và mỗi ServiceBooking sẽ tạo một FolioItem.
    - [x] Cập nhật trạng thái dịch vụ (PENDING → FINISHED) và tính tiền dịch vụ vào Folio khi hoàn thành.

- [x] Thêm tính năng sửa đổi đăng ký dịch vụ:
    - [x] Chỉ cho phép sửa đổi khi dịch vụ đang ở trạng thái PENDING.
    - [x] Cập nhật lại FolioItem và tổng tiền trong Folio tương ứng.

- [x] Thêm tính năng hủy đăng ký dịch vụ:
    - [x] Chỉ cho phép hủy khi dịch vụ đang ở trạng thái PENDING.
    - [x] Cập nhật lại FolioItem và tổng tiền trong Folio tương ứng.

- [x] Thêm tính năng Check-in/check-out
    - [x] **Check-in**: ✅ Hoàn thành
        - [x] Cập nhật trạng thái đặt phòng thành IN_HOUSE.
        - [x] Lưu thời gian check-in thực tế vào từng `reservation_room.actual_check_in`.
        - [x] Chỉ được check-in khi trạng thái đặt phòng là CONFIRMED.
        - [x] Gán ReservationRoom vào phòng cụ thể (RoomEntity):
            - [x] Manual assignment: lễ tân gửi `reservationRoomId + roomId`
            - [x] Auto-assign: hệ thống tự chọn phòng AVAILABLE theo roomClass (nếu `autoAssign=true`)
        - [x] Validate: phòng phải AVAILABLE, cùng roomClass, không bị gán trùng trong cùng request
        - [x] Cập nhật room status → OCCUPIED, allocation status → CHECKED_IN
        - [x] Viết test: 3 unit tests (manual, auto, reject invalid status)
        - [x] Viết API test: 2 integration tests (success, business error mapping)
        - [x] Xử lý trường hợp check-in sớm hơn ngày dự kiến theo business rule (tính thêm tiền tuỳ vào tg
          check-in).
        - [x] Viết api để lấy tất cả các phòng available theo room class trong khoảng thời gian nhất định (Trong
          trường hợp lễ tân tự gán phòng).

    - [x] **Check-out**: ✅ Hoàn thành
        - [x] Checkout TỪNG PHÒNG (reservation room), không phải cả reservation
        - [x] API GET `/api/v1/reservation-rooms/{id}/folio` - Lấy folio details (room info, occupants, folio items,
          balance)
        - [x] API POST `/api/v1/reservation-rooms/{id}/check-out` - Check-out phòng cụ thể
        - [x] Cập nhật trạng thái phòng thành CHECKED_OUT
        - [x] Cập nhật thời gian check-out thực tế
        - [x] Tính phí check-out muộn (BR-09), cập nhật Folio tương ứng
        - [x] Validation: chỉ checkout khi status IN_HOUSE và không có dịch vụ PENDING
        - [x] Khi tất cả phòng checked out thì tự động update reservation status → CHECKED_OUT
        - [x] Viết unit tests (7 test cases)
        - [x] Viết API integration tests (2 test cases)
        - [x] Cập nhật physical room status → DIRTY khi check-out
        - [x] Tạo service riêng: `ReservationRoomService` để quản lý operations từng phòng
        - [x] Response trả về list folio items (các khoản phát sinh) kèm room occupants

- [x] Thêm tính năng xử lý thanh toán:
    - [x] Tạo API `POST /api/v1/reservation-rooms/{id}/payment` để thanh toán cho từng phòng
    - [x] Folio details đã có sẵn trong API `GET /api/v1/reservation-rooms/{id}/folio`
    - [x] Payment processing:
        - [x] Frontend gửi list **FolioItemIds** mà khách chọn để thanh toán (không auto allocate)
        - [x] Gửi kèm **cashAmount** (tiền mặt/thẻ) và **depositAmount** (số tiền deposit muốn dùng)
        - [x] Validate deposit available: `depositAvailable = totalDeposit - depositUsed`
        - [x] Validate payment amount covers all selected items
        - [x] Tạo PaymentTransaction + PaymentAllocation cho từng FolioItem đã chọn
        - [x] Mark selected FolioItem status → PAID
        - [x] Update `reservation.depositUsed` khi dùng deposit
        - [x] Response trả về: cashAmount, depositUsed, totalAmount, remainingBalance, depositAvailable
    - [x] Cập nhật folio balance và đóng folio (status CLOSED) khi balance = 0
    - [x] Khi tất cả phòng đã thanh toán đủ → cập nhật Reservation status thành FINISHED
    - [x] Thêm field `deposit_used` vào reservation table và entity

---

### B) Done (lịch sử các nhóm nhiệm vụ lớn đã hoàn thành)

> Dưới đây là các nhóm nhiệm vụ lớn đã hoàn thành, giúp hệ thống ổn định, chuẩn hóa và dễ mở rộng:

#### 1. Chuẩn hóa Enum & Converter

- Xóa enum sai: `RoomClass` (vì đây là bảng dữ liệu)
- Chuẩn hóa enum theo DDL cho các trường trạng thái/loại (reservation, room, payment, service...)
- Áp dụng BaseEnumStringConverter cho tất cả các enum
- Bổ sung các enum còn thiếu theo comment trong `ddl.sql`

#### 2. API Đặt phòng & Quản lý khách hàng

- Tách module API + Service theo domain: Reservation, Customer, Room
- Chuyển API tìm kiếm khách hàng theo CCCD sang CustomerController
- Sửa tên endpoint tạo đặt phòng từ bookings sang reservations, đồng bộ với tên bảng
- Xóa BookingService/BookingServiceImpl cũ
- Tạo các repository: RoomClassRepository, ReservationRepository, ReservationRoomAllocationRepository
- Viết tài liệu API (BOOKING_API_DOCS.md)

#### 3. Xử lý logic đặt phòng & Room Allocation

- Refactor createReservation thành các bước nhỏ (validate, resolve customer, validate room class, save reservation,
  create allocations, build response)
- Đổi field `quantity` → `numberOfPeople` trong DDL và entity
- Đổi RoomClassQuantity về 2 tham số (roomClassId, numberOfPeople)
- Cập nhật toàn bộ unit test liên quan

#### 4. Chuẩn hóa xử lý lỗi & Response

- Tạo ErrorResponse thống nhất cho mọi API
- Tạo GlobalExceptionHandler xử lý ApiException, validation error, fallback system error
- Chuẩn hóa mã lỗi qua ErrorCode, mở rộng đầy đủ domain
- Chuyển toàn bộ utils exception sang kế thừa ApiException
- Refactor ConvertibleUtils dùng ApiException
- Tạo ApiResponse<T> wrapper cho tất cả success response
- Áp dụng ApiResponse vào toàn bộ controller

#### 5. Unit Test & Kiểm thử

- Viết 13 unit test cho createReservation (bao gồm happy path, validation, exception, edge case, verify repository call)

#### 6. Xử lý Folio & Payment

- Sau khi tạo đơn đặt phòng, tính toán tiền cọc, tạo FolioEntity và PaymentTransactionEntity tạm thời
- Công thức tính tiền cọc: depositAmount = totalRoomCost * 0.20
- Công thức tính tổng tiền phòng: totalRoomCost = sum(roomClassPrice * numberOfPeople)

#### 7. Refactor & Chuẩn hóa code

- Tách các nhóm Enum trong ErrorCode thành các category rõ ràng
- Sửa trường reservationId trong FolioItemEntity và FolioEntity thành reservationRoomAllocationId

#### 8. Cập nhật Reservation + ổn định build/test

- Thêm API `PUT /api/v1/reservations/{reservationId}` để cập nhật reservation.
- Bổ sung rule khóa cập nhật trong 24h trước check-in (`RESERVATION_UPDATE_LOCKED`).
- Refactor luồng update: cập nhật reservation + thay thế allocations + tạo lại folio deposit item theo dữ liệu mới.
- Bổ sung test cho service update reservation và test API cho endpoint PUT.
- Sửa lỗi compile ở payment: dùng enum typed trong `PaymentServiceImpl`, thêm `findByTransactionReference` trong
  `PaymentTransactionRepository`.
- Gộp request DTO cho create/update thành `ReservationRequest` (thay cho `CreateBookingRequest`).
- Rename DTO: `BookingResponse` → `ReservationResponse` để thống nhất ngữ nghĩa với domain model (giữ nguyên response
  fields `bookingId`/`bookingCode` cho API compatibility).
- Chuẩn hóa tên test helper: `createBookingRequest()` → `createReservationRequest()`.
- Đồng bộ mocks/assertions theo dependency refactor (RoomAllocationService + FolioService).

#### 9. Hủy đặt phòng (Cancellation)

- Thêm API `DELETE /api/v1/reservations/{reservationId}` để hủy reservation.
- Áp dụng chính sách hoàn tiền: hủy >24h trước check-in → hoàn đủ tiền cọc (tạo `FolioItem` type `ADJUSTMENT` với
  negative amount), hủy <24h → không hoàn tiền (tạo `FolioItem` ghi nhận phí hủy).
- Bổ sung validation: không cho ph��p hủy khi status là `CANCELLED`/`IN_HOUSE`/`CHECKED_OUT`/`FINISHED`.
- Thêm `ErrorCode`: `RESERVATION_ALREADY_CANCELED`, `RESERVATION_CANCEL_NOT_ALLOWED`.
- Mở rộng `FolioService`/`FolioItemService` với `createRefundItem()` và `createCancellationFeeItem()`.
- Viết test (7 unit tests + 3 integration tests) cho cancel reservation.

#### 10. Đặt dịch vụ (Service Booking)

- Thêm API `POST /api/v1/reservation-rooms/{reservationRoomId}/services` để đặt dịch vụ cho phòng đã phân bổ.
- Service booking liên kết với `ReservationRoomEntity` (không phải `Reservation`), phù hợp nghiệp vụ: mỗi phòng có dịch
  vụ riêng.
- Validation: chỉ cho phép đặt khi reservation status = `IN_HOUSE` (checked-in).
- Tạo `ServiceBookingEntity` với status = `PENDING`, lưu `priceAtBooking` để lock giá tại thời điểm đặt.
- Thêm `ErrorCode`: `SERVICE_NOT_FOUND`, `SERVICE_INACTIVE`, `SERVICE_BOOKING_NOT_ALLOWED`,
  `RESERVATION_ROOM_NOT_FOUND`.
- Tạo DTO: `ServiceBookingRequest`, `ServiceBookingResponse`.
- Tạo repository: `ServiceRepository`, `ServiceBookingRepository`.
- Viết test (8 unit tests + 3 integration tests) cho create service booking.
- **Note**: FolioItem chỉ được tạo khi status = `FINISHED` (chưa implement trong scope này).

#### 11. Sửa/Hủy đặt dịch vụ (Update & Cancel ServiceBooking)

- Thêm API `PUT /api/v1/reservation-rooms/{reservationRoomId}/services/{serviceBookingId}` để cập nhật dịch vụ.
- Thêm API `DELETE /api/v1/reservation-rooms/{reservationRoomId}/services/{serviceBookingId}` để hủy dịch vụ.
- Validation: chỉ cho phép cập nhật/hủy khi `ServiceBooking.status = PENDING`.
- Folio synchronization: `upsertServiceCharge()` để cập nhật charge item, `voidServiceCharge()` để hủy + adjust balance.
- Mở rộng `FolioItemRepository` với `findByServiceBookingEntityAndIsActiveTrue()` để tìm active charge item.
- Mở rộng `FolioItemService` với `createServiceChargeItem()`, `updateServiceChargeItem()`, `voidServiceChargeItem()`.
- Thêm constants: `SERVICE_BOOKING_CHARGE`, `SERVICE_BOOKING_CANCELED` trong `Description.java`.
- Scoped repository lookup: `findByIdAndReservationRoomEntity_Id()` để chắc chắn service booking thuộc phòng đó.

#### 12. Check-in Reservation

- Thêm API `POST /api/v1/reservations/{reservationId}/check-in` để check-in toàn bộ reservation.
- Request DTO: `ReservationCheckInRequest(autoAssign, List<ReservationRoomCheckInRequest(reservationRoomId, roomId)>)`.
- Validation:
    - Chỉ check-in khi `ReservationStatus = CONFIRMED`
    - Các `reservationRoomId` phải thuộc reservation hiện tại
    - Không duplicate assignment trong cùng request
- Assignment logic:
    - **Manual**: Frontend gửi list (reservationRoomId, roomId) → validate room AVAILABLE, active, đúng roomClass
    - **Auto-assign**: Nếu `autoAssign=true`, hệ thống tự tìm phòng AVAILABLE theo roomClass, tránh trùng với
      `usedRoomIds`
- Cập nhật:
    - `ReservationRoomEntity`: set `roomEntity`, `status=CHECKED_IN`, `actualCheckIn=Instant.now()`
    - `RoomEntity`: set `status=OCCUPIED`
    - `ReservationEntity`: set `status=IN_HOUSE`
- ErrorCode mới: `RESERVATION_CHECKIN_NOT_ALLOWED`, `ROOM_NOT_AVAILABLE`, `ROOM_CLASS_MISMATCH`,
  `RESERVATION_ROOM_ASSIGNMENT_REQUIRED`
- Thêm DDL: cột `actual_check_in` trong `reservation_room`
- Repository mới: `findByReservationEntity_IdAndIsActiveTrue()`,
  `findByRoomClassEntity_IdAndStatusAndIsActiveTrueOrderByIdAsc()`
- Test: 3 unit tests (manual, auto, reject invalid) + 2 integration tests (success, error mapping)

#### 13. Refactor Naming Consistency

- Rename enum: `ReservationRoomAllocationStatus` → `ReservationRoomStatus`
- Rename repository: `ReservationRoomAllocationRepository` → `ReservationRoomRepository`
- Cập nhật toàn bộ imports trong service/test files
- Sửa field name trong `FolioEntity`: `reservationRoomAllocation` → `reservationRoom`
- Fix TODO trong `FolioServiceImpl`: refactor từ delta-based update sang recalculate totals từ đầu (tránh lỗi cộng dồn)
- Fix TODO trong `ServiceBookingEntity`: xóa OneToOne mapping (owning side ở `FolioItemEntity`)
- Thêm repository method: `FolioItemRepository.findByFolioEntity_IdAndIsActiveTrue()`
- Thêm service method: `FolioItemService.calculateTotalCharges()` để tính tổng từ active items

#### 14. API hỗ trợ gán phòng manual cho check-in

- Thêm DTO: `AvailableRoomResponse(roomId, roomNumber)`
- Thêm DTO: `RoomClassAvailableRoomsResponse(roomClass, List<AvailableRoomResponse>)`
- Mở rộng `RoomRepository`:
    - `findAvailableRoomsForPeriod()` - lấy tất cả phòng available group theo roomClass
    - `findAvailableRoomsForPeriodByRoomClassId()` - lấy phòng available theo 1 roomClassId cụ thể
- Mở rộng `RoomService`:
    - `getAvailableRoomsForAssignment()` - trả list grouped
    - `getAvailableRoomsByRoomClassIdForAssignment()` - trả list theo roomClassId
- Thêm endpoints:
    - `GET /api/v1/rooms/available-for-assignment` - tất cả room classes
    - `GET /api/v1/rooms/available-for-assignment/by-room-class?roomClassId=...` - theo 1 room class
- Validate date range dùng chung với reservation flow
- Test: 5 unit tests + 3 integration tests

> Lưu lại lịch sử để AI không đề xuất làm lại những gì đã có.