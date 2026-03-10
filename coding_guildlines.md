# Coding Guidelines (Technical Standards) [SỬA THEO DỰ ÁN]

## Environment

* **Build tool:** Maven.

## Language & Standards

* **Backend:** Java 21, Spring Boot 4.x, JPA/Hibernate, RESTful API.
* **Frontend:** React 19.x, JavaScript.
* **Database:** MySQL 8.x.

## Architecture

* **Layered Architecture**.

## Backend Coding Style & Safety

### General

* Sử dụng Lombok cho Entities.
* **Sử dụng Java Records cho DTOs** (immutable, clean, type-safe).
* Tránh code boilerplate.
* Các API phải tuân thủ RESTful conventions và có version trong URL (ví dụ: `/api/v1/users`).
* Khi có logic phức tạp trong service, hãy tách thành private method nhỏ để tăng tính modular và dễ đọc.

### DTOs & Response Design

* Luôn sử dụng Java Records cho DTOs.
* Phân biệt rõ:
    * request DTO: `dto.request`
    * response DTO: `dto.response`
* Khi trả về dữ liệu, **không** chỉ gửi id rồi bắt frontend fetch thêm; hãy trả về toàn bộ object cần thiết để giảm số
  lần gọi API.
* Khi return response, luôn trả về DTO tương ứng, tránh trả về entity trực tiếp (tránh lộ thông tin nhạy cảm hoặc đệ quy
  lồng nhau).
* DTOs liên quan tiền tệ: dùng `BigDecimal` thay vì `Double`/`Float`.
* **Nếu 1 resource có thể xuất hiện nhiều lần trong create request** (ví dụ: `roomClassQuantities` add cùng 1 roomClass
  2 lần):
    * **dùng `List` thay vì `Map`**
    * Tạo wrapper class/record rõ nghĩa (ví dụ: `RoomClassQuantity(roomClassId, quantity)`).

> Ghi chú: Quy tắc "wrap List data từ controller trong Map.of(...)" nên hạn chế vì khó chuẩn hoá schema; nếu dự án vẫn
> muốn dùng, hãy định nghĩa chuẩn wrapper/response cố định thay vì Map ad-hoc.

### Converters & Mapping

* Ưu tiên dùng MapStruct để map giữa entity và DTO.
* Tạo converters trong package `com.product.hms.converters`.
* Nếu field name trùng nhau, MapStruct tự động map → không cần `@Mapping`.
* Enum fields: dùng `BaseEnumStringConverter` để lưu dưới dạng string trong DB.

### Search / Sort

* Khi search/sort ưu tiên dùng `SpecificationUtils` đã viết sẵn.

### Update

* Khi update entity, dùng `MergeObjectUtils` để tránh mất dữ liệu.

### Delete

* Dùng soft delete (`is_active = false`) thay vì hard delete.

### API Endpoint Design

* Endpoint phải đặt sát nghĩa nhất có thể, tránh từ chung chung gây lặp:
    * Ví dụ: `/api/v1/rooms-class/available` thay vì `/api/v1/rooms/available` để tránh nhầm lẫn.
* CRUD operations cơ bản của 1 domain nên tập trung trong 1 controller/service.
* Tách biệt rõ ràng giữa các module theo domain logic (Customer, Room, Reservation, Service...).

### Error Handling

* Tất cả API trả về response có cấu trúc thống nhất, bao gồm cả lỗi (ví dụ: `ErrorResponse` record).
* Ưu tiên dùng `ApiException`, `ErrorCode` để chuẩn hoá lỗi thay vì ném `RuntimeException`.

---

## Service Implementation Guidelines (giảm “service dài ngoằng”, dễ đọc & dễ sửa)

### 1) Giới hạn độ dài & tiêu chí bắt buộc tách service

* Một `*ServiceImpl` nên tập trung vào **1 use-case chính**.
* Nếu thỏa **một** trong các điều kiện sau → **bắt buộc refactor/tách**:
    * Class service > **250–300 dòng** (không tính import).
    * Có > **10 methods** (public + private) hoặc xử lý > **2 luồng nghiệp vụ lớn** (create/update + cancel +
      check-in...).
    * Một method > **40–60 dòng** hoặc có quá nhiều nhánh if/for lồng nhau.
* Ưu tiên tách theo **use-case/application service**, ví dụ:
    * `ReservationBookingService` (create/update)
    * `ReservationCancellationService` (cancel/refund policy)
    * `ReservationCheckInService` (check-in + assign room)
* `ReservationServiceImpl` (nếu cần giữ interface) chỉ nên làm nhiệm vụ **delegate/orchestrate** sang các service trên.

### 2) Không trộn nhiều loại logic trong 1 method

Trong 1 method service, tránh trộn lẫn (dẫn tới method dài và khó test):

1) validate request
2) load data (repository)
3) tính toán nghiệp vụ (pricing, deadline, refund rule…)
4) mutate entity/state
5) persistence (save)
6) mapping response (DTO)

Khuyến nghị pattern:

* `validateXxx(...)`
* `loadXxxOrThrow(...)`
* `applyXxxChange(...)` (mutate state)
* `persistXxx(...)`
* `toResponse(...)`

### 3) “Policy / Calculator” objects cho rule phức tạp

Các rule có tính “policy” hoặc “tính toán” nên tách thành class riêng để:

- không lặp logic
- giảm độ dài service
- dễ unit test

Ví dụ:

* `ReservationTimePolicy`:
    * `canUpdate(reservation, now)`
    * `isRefundEligible(reservation, now)`
* `ReservationPricingCalculator`:
    * tính tổng tiền phòng (base + extra people fee)
    * tính deposit

### 4) Quy tắc về thời gian (Time): không gọi `now()` trực tiếp trong service

* **Không gọi trực tiếp** `Instant.now()`, `LocalDate.now()`, `new Date()` trong service.
* Bắt buộc inject `java.time.Clock`:
    * Production: `Clock.systemUTC()`
    * Test: `Clock.fixed(...)`
* Chỉ dùng:
    * `Instant.now(clock)`
    * `LocalDate.now(clock)`
* Tất cả logic deadline/time-window phải thống nhất timezone (khuyến nghị **UTC**).

### 5) Tránh cập nhật số liệu “cộng dồn” thủ công nếu dễ sai

Với các field kiểu tổng (`totalCharges`, `totalPaid`, `balance`):

* Ưu tiên **recalculate** từ dữ liệu chi tiết (folio items, payment allocations/transactions) thay vì cộng/trừ delta ở
  nhiều nơi.
* Nếu bắt buộc dùng delta, phải gom về **1 hàm duy nhất** (vd `applyChargeDelta(...)`) và có unit test cover.

### 6) Naming để code “tự nói”

* Đặt tên method theo hành vi nghiệp vụ:
    * `validateCheckInAllowed`, `assignRoomsForCheckIn`, `createAllocationsAndFolios`
* Tránh tên chung chung kiểu `handle`, `process`, `doSomething`.
* Comment ưu tiên theo nghiệp vụ (“tại sao”) hơn là theo kỹ thuật (“làm gì” — vì code đã nói rồi).

---

## Frontend Coding Style

* Sử dụng JSDoc cho tất cả function, component, props và types (dùng typedef khi tạo type mới).

## Naming Conventions

* Backend:
    * **Classes/Interfaces:** PascalCase (ví dụ: `UserService.java`, `UserController.java`).
    * **Variables/Methods:** camelCase (ví dụ: `createUser()`, `getUserById()`).
* Frontend:
    * **Components/Types:** PascalCase (ví dụ: `UserCard.jsx`, `UserType.js`).

## Formatting

* Sử dụng Prettier với cấu hình chuẩn, đảm bảo code luôn được format nhất quán.

## Testing

* Spring Boot Test, JUnit 5.

---

## Testing Guidelines (giảm test khó đọc, giảm flakiness do thời gian)

### 1) Cấu trúc test (Given / When / Then)

* Mỗi test phải có 3 phần rõ ràng:
    * **Given:** setup dữ liệu + mock
    * **When:** gọi method cần test
    * **Then:** assert + verify
* Tránh nhồi setup dài trong từng test.

### 2) Test Data Builders / Factories (bắt buộc khi entity phức tạp)

* Với entity/DTO nhiều field (Reservation/Room/Customer/Allocation...), ưu tiên tạo `TestDataFactory` hoặc builder:
    * `aConfirmedReservation()`
    * `aPendingAllocation(reservation, roomClass)`
    * `anAvailableRoom(roomClass)`
* Mục tiêu: mỗi test chỉ cần ~**5–15 dòng** setup chính để đọc như kịch bản nghiệp vụ.

### 3) Thời gian trong test: tuyệt đối tránh `Instant.now()`

* Trong unit test, **không dùng** `Instant.now()` / `LocalDate.now()` trực tiếp.
* Dùng 1 mốc cố định:
    * `Instant.parse("2026-03-08T00:00:00Z")`
* Nếu code đã inject `Clock`, test dùng `Clock.fixed(...)` để thời gian luôn ổn định.

### 4) Verify có ý nghĩa (tránh verify(any()) quá nhiều)

* Hạn chế:
    * `verify(repo).saveAll(any())`
      vì quá “lỏng”, test vẫn pass dù save sai dữ liệu.
* Ưu tiên:
    * `ArgumentCaptor` để kiểm tra entity save đúng field
    * `argThat(...)` để kiểm tra list/đối tượng lưu đúng phần tử/giá trị

### 5) 1 test = 1 scenario

* 1 test chỉ nên kiểm tra **1 happy path** hoặc **1 lý do fail**.
* Nếu test dài, tách thành nhiều test nhỏ theo case.

### 6) Naming & file naming

* Tên file nên khớp tên class:
    * `ReservationServiceCheckInTest.java` chứa `ReservationServiceCheckInTest`
* `@DisplayName` mô tả behavior nghiệp vụ, tránh mô tả chi tiết implementation.