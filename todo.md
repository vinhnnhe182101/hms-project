# 🗺️ PROJECT ROADMAP: Hotel Management System

## 🎯 Main Goal

* [ ] Hoàn thành một hệ thống quản lý khách sạn cho vai trò lễ tân, bao gồm các chức năng như đặt phòng,
  check-in/check-out, và quản lý dịch vụ.

---

## 🚀 Active Tasks (Current Sprint)

> **Trọng tâm hiện tại:** Hoàn thiện lớp Validation và xử lý các kịch bản vận hành nâng cao.

* [ ] **Validation Layer**:
* Áp dụng `@Valid` cho toàn bộ Request Body trong Controller.
* Triển khai `Custom Validators` cho các logic phức tạp (ví dụ: validate khoảng ngày, validate danh sách phòng không
  trùng lặp).


* [ ] **Overbooking & Room Upgrade Management**:
* Xử lý kịch bản khi check-in nhưng loại phòng khách đặt đã hết (do lỗi hệ thống hoặc overbooking).
* Thêm logic nâng hạng phòng miễn phí (Complimentary Upgrade) theo Business Rule.


* [ ] **Reporting Service (Backend)**:
* API thống kê công suất phòng (Occupancy Rate).
* API báo cáo doanh thu theo ngày/tháng/năm.

---

## 📌 Temporary Notes

* **@Valid Annotation**: Tạm thời chưa thêm để tập trung code logic core, sẽ tiến hành refactor hàng loạt sau khi xong
  các feature chính để tránh rườm rà code.
* **Physical Room Status**: Đảm bảo trạng thái `DIRTY` sau khi check-out được cập nhật chính xác để bộ phận buồng phòng
  nắm bắt.

---

## 🗂️ Backlog

> Những tính năng hoặc cải tiến sẽ làm sau khi xong Active Tasks.

* **Frontend Development**:
* Xây dựng Dashboard cho lễ tân.
* Giao diện sơ đồ phòng (Room Map) hiển thị trạng thái thời gian thực.
* Form đặt phòng và thanh toán trực quan.


* **System Enhancements**:
* Tích hợp gửi Email xác nhận đặt phòng tự động.
* Quản lý lịch sử bảo trì phòng.

---

## ✅ Done (Lịch sử nhiệm vụ đã hoàn thành)

### A) Mới hoàn thành (Sprint hiện tại)

* [x] **Thanh toán Offline**: API đánh dấu thanh toán (`mark as paid`) cho khách thanh toán tiền mặt/quẹt thẻ tại quầy.
* [x] **Đổi phòng (Room Change)**:
* Hỗ trợ đổi cùng hạng (`Transfer`) và nâng hạng (`Upgrade`).
* Phân loại phí: Do khách yêu cầu (tính phí) vs. Do lỗi phòng (miễn phí).
* Tự động cập nhật Folio Item cho phí đổi phòng.
* Validation: Chỉ đổi khi trạng thái là `IN_HOUSE` và phòng đích phải `AVAILABLE`.


* [x] **Check-in/Check-out**:
* Hoàn thiện luồng Check-in (Manual & Auto-assign).
* Xử lý phí Check-in sớm/Check-out muộn theo giờ quy định.
* Tự động cập nhật trạng thái phòng (`OCCUPIED` / `DIRTY`).


* [x] **Thanh toán & Folio**:
* API thanh toán linh hoạt cho từng phòng.
* Khấu trừ tiền cọc (Deposit) và tính toán số dư thực tế.
* Tự động chuyển trạng thái Reservation sang `FINISHED` khi tất cả các phòng đã thanh toán đủ.


* [x] **Quản lý Dịch vụ**: Đặt dịch vụ, cập nhật số lượng và hủy dịch vụ kèm theo đồng bộ hóa hóa đơn (Folio).
* [x] **Search Engine**: API tìm kiếm động (Specification) cho Dịch vụ, Đặt phòng và Phòng.

### B) Nền tảng hệ thống (Core Architecture)

1. **Chuẩn hóa Enum & Converter**: Áp dụng `BaseEnumStringConverter` toàn hệ thống.
2. **Standard Response**: Cấu trúc `ApiResponse<T>` và `ErrorResponse` thống nhất.
3. **Global Exception Handling**: Xử lý lỗi tập trung qua `ErrorCode`.
4. **Reservation Logic**: Tách nhỏ Service (Allocation, Folio, Converter) để dễ bảo trì.
5. **Customer Management**: Tách module quản lý khách hàng theo CCCD.
6. **Cancellation Policy**: Tự động tính phí hủy hoặc hoàn tiền cọc dựa trên mốc 24h trước check-in.
7. **Folio & Payment Transaction**: Kiến trúc lưu trữ giao dịch và phân bổ thanh toán (`PaymentAllocation`).

---