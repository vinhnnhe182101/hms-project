# 📓 PROJECT LESSONS & PITFALLS

# Template

## [Tên vấn đề/Lỗi đã gặp]

- **Thời gian:** [Ngày/Tháng/Năm]
- **Bối cảnh:** [Đang làm tính năng gì thì gặp lỗi?]
- **Vấn đề:** [Mô tả ngắn gọn lỗi hoặc khó khăn gặp phải]
- **Nguyên nhân:** [Tại sao nó xảy ra? Do thư viện, do logic hay do phần cứng?]
- **Giải pháp:** [Cách đã xử lý thành công]
- **Quy tắc mới:** [AI phải làm gì để không lặp lại lỗi này? Ví dụ: "Luôn kiểm tra con trỏ NULL trước khi gọi hàm X"]

---

## [Nhầm lẫn giữa enum domain và bảng master data]

- **Thời gian:** 04/03/2026
- **Bối cảnh:** Refactor API đặt phòng, chuẩn hóa enum theo DDL và áp dụng vào repository/entity.
- **Vấn đề:** Đã tạo enum `RoomClass` trong khi `room_class` thực chất là bảng master data trong DB. Đồng thời một số
  chỗ vẫn hardcode status string trong query/service.
- **Nguyên nhân:** Chưa phân biệt rõ dữ liệu dạng "danh mục cố định trong code" (enum) với dữ liệu "danh mục quản trị
  trong DB" (master table), và chưa bám sát comment enum trong `ddl.sql` trước khi code.
- **Giải pháp:** Xóa enum `RoomClass`, giữ `RoomClassEntity` + bảng `room_class`; thêm enum đúng theo DDL cho các trường
  trạng thái/loại; refactor `RoomRepository` và service để dùng enum typed thay vì hardcode string.
- **Quy tắc mới:** Trước khi tạo enum, luôn kiểm tra `ddl.sql`: nếu là bảng master data thì **không** tạo enum tương
  ứng; chỉ dùng enum cho cột trạng thái/loại được định nghĩa bằng tập giá trị cố định. Khi query/service dùng
  status/type, luôn ưu tiên enum typed, không hardcode chuỗi.

---

## [God-method trong service tạo reservation]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Refactor luồng tạo reservation sau khi tách API/Service theo domain.
- **Vấn đề:** `createReservation` chứa quá nhiều trách nhiệm trong một method (validate, xử lý customer, kiểm tra room
  class, lưu reservation, tạo allocations, build response), gây khó đọc và khó mở rộng.
- **Nguyên nhân:** Dồn toàn bộ nghiệp vụ vào một điểm xử lý thay vì chia nhỏ theo step; truy vấn `RoomClassEntity` bị
  lặp ở nhiều đoạn.
- **Giải pháp:** Tách thành các private method theo flow nghiệp vụ (
  `validate -> resolve customer -> load/validate room classes -> save reservation -> create allocations -> build response`),
  đồng thời cache `RoomClassEntity` theo `roomClassId` để tái sử dụng trong cùng transaction.
- **Quy tắc mới:** Service method public chỉ nên đóng vai trò orchestration; mỗi step nghiệp vụ tách method riêng, tên
  rõ nghĩa, tránh query lặp trong cùng transaction. Khi request cho phép trùng `roomClassId`, xử lý merge rõ ràng ở bước
  build response/aggregate.

---

## [Thiếu contract lỗi thống nhất giữa các API]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Ưu tiên hoàn thiện Config API advice trước khi làm nghiệp vụ tiền cọc.
- **Vấn đề:** Service ném `RuntimeException` rải rác khiến lỗi trả về không đồng nhất, khó cho frontend xử lý theo mã
  lỗi.
- **Nguyên nhân:** Chưa có `ErrorResponse` chuẩn và chưa có cơ chế map exception tập trung ở tầng API.
- **Giải pháp:** Tạo `ErrorResponse` record + `GlobalExceptionHandler`; chuẩn hóa exception theo `ApiException` và
  `ErrorCode`; thay các điểm ném `RuntimeException` chính trong reservation/customer sang exception có mã lỗi rõ ràng;
  mở rộng `ErrorCode` để cover toàn bộ domain + utils exception; chuyển tất cả utils exception (page, specification,
  mapping) sang kế thừa `ApiException`.
- **Quy tắc mới:** Mọi lỗi nghiệp vụ/not-found/invalid request/technical phải đi qua custom exception có `ErrorCode`;
  controller/service/utils không tự ném `RuntimeException` hoặc tạo cấu trúc lỗi ad-hoc. Bổ sung `ErrorCode` mới khi cần
  thiết thay vì hardcode message string.

---

## [DTO Design & Response Structure]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Áp dụng MapStruct và chuẩn hóa DTO theo quy định dự án.
- **Vấn đề:** Ban đầu dùng Map để wrap response với `AvailableRoomResponse` (Map có roomClassId), khiến response phức
  tạp và lặp lại dữ liệu. Cũng tạo `roomClassQuantities` dưới dạng Map thay vì List, không phù hợp khi 1 roomClass có
  thể add 2 lần với số lượng khác nhau.
- **Nguyên nhân:** Chưa phân biệt rõ khi nào dùng Map vs List, chưa hình dung cấu trúc response cho frontend và case
  khách hàng có nhu cầu add cùng 1 resource nhiều lần.
- **Giải pháp:**
    - Dùng `List<RoomClassAvailabilityResponse>` ở service level, controller wrap trong
      `Map.of("RoomClassAvailabilityResponses", List...)`
    - Tạo `RoomClassQuantity` wrapper class chứa `roomClassId + quantity` để thay thế Map
    - Luôn phân biệt request DTO (dto.request) và response DTO (dto.response)
    - Sử dụng Java Records cho DTOs (immutable, clean, type-safe)
- **Quy tắc mới:**
    - Khi response chứa List, wrap trong Map ở controller với key meaningful
    - Khi create request có thể add cùng 1 resource nhiều lần → dùng List + wrapper class, không dùng Map
    - DTOs liên quan tiền tệ phải dùng `BigDecimal` thay vì `Double`/`Float`
    - Luôn trả về toàn bộ object cần thiết (không chỉ id) để giảm số lần gọi API

---

## [MapStruct Converters - No Manual Mapping]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Áp dụng MapStruct để map entity ↔ DTO thay vì viết manual.
- **Vấn đề:** Viết manual mapping code dẫn đến boilerplate, dễ sai xót khi có field mới.
- **Nguyên nhân:** Chưa tận dụng MapStruct tốt, chưa biết MapStruct tự động map khi field name trùng.
- **Giải pháp:** Tạo interface converter trong package `com.product.hms.converters` với annotation
  `@Mapper(componentModel = "spring")`, MapStruct tự generate implementation.
- **Quy tắc mới:**
    - Luôn dùng MapStruct converter, không viết manual mapper
    - Nếu field name entity và DTO trùng → MapStruct tự map, không cần `@Mapping`
    - Chỉ dùng `@Mapping` khi field name khác nhau hoặc cần custom logic
    - Converters trong package `com.product.hms.converters` để dễ quản lý

---

## [Check-out Implementation & Per-Room Operation]

- **Thời gian:** 08/03/2026
- **Bối cảnh:** Triển khai tính năng check-out với late check-out fee theo BR-09.
- **Vấn đề:** Ban đầu implement check-out ở reservation level (gửi list room IDs), nhưng business logic thực tế là
  checkout TỪNG PHÒNG riêng lẻ. Frontend cần lấy folio details của TỪNG PHÒNG trước khi checkout, không phải của cả
  reservation.
- **Nguyên nhân:** Chưa hiểu rõ workflow: Frontend sẽ hiển thị danh sách phòng → User chọn 1 phòng → Xem folio items của
  phòng đó → Checkout phòng đó. Không phải checkout nhiều phòng cùng lúc.
- **Giải pháp:**
    - Tạo service riêng `ReservationRoomService` để quản lý operations của TỪNG phòng
    - API GET `/api/v1/reservation-rooms/{id}/folio` - Trả về folio details (room info, occupants, list folio items,
      balance)
    - API POST `/api/v1/reservation-rooms/{id}/check-out` - Checkout 1 phòng cụ thể (không cần request body)
    - Xóa logic checkout cũ từ `ReservationService` (đã implement sai ở reservation level)
    - Response trả về **list folio items** thay vì wrap trong Map/Object phức tạp
    - Tạo `RoomOccupantRepository` để lấy thông tin chủ phòng (occupants)
- **Quy tắc mới:**
    - Khi nghiệp vụ là "per item operation" (checkout từng phòng, xử lý từng dịch vụ), tạo service/api riêng cho entity
      đó
    - Luôn hỏi rõ workflow từ Frontend perspective: họ cần gì, khi nào, ở đâu
    - Response cho checkout preview nên trả về **list items** trực tiếp, không wrap phức tạp
    - Tách biệt rõ: Reservation operations (create, update, cancel) vs ReservationRoom operations (checkout, folio)
    - Service naming: `ReservationService` vs `ReservationRoomService` để phân biệt scope

---

## [Payment Processing & Folio Lifecycle]

- **Thời gian:** 08/03/2026
- **Bối cảnh:** Triển khai tính năng thanh toán sau khi check-out.
- **Vấn đề:** Cần xác định đúng workflow thanh toán: thanh toán từng phòng hay cả reservation? Khi nào update
  reservation status thành FINISHED?
- **Nguyên nhân:** Chưa rõ về lifecycle của Folio và Payment trong hệ thống khách sạn.
- **Giải pháp:**
    - Payment xử lý **TỪNG PHÒNG** (reservation room), không phải cả reservation - consistent với pattern đã có
    - API `POST /api/v1/reservation-rooms/{id}/payment` - thanh toán cho 1 phòng cụ thể
    - Payment allocation: Phân bổ số tiền thanh toán vào các FolioItem UNPAID theo thứ tự (FIFO-like)
    - Mark FolioItem → PAID khi được thanh toán đủ
    - Đóng Folio (status CLOSED) khi balance = 0
    - Reservation status → FINISHED khi: (1) status = CHECKED_OUT và (2) tất cả phòng đã thanh toán đủ (balance = 0)
    - Validate không cho thanh toán cho folio đã CLOSED
- **Quy tắc mới:**
    - Payment processing pattern: Create PaymentTransaction → Create PaymentAllocation for each item → Update folio
      totals → Close folio if fully paid
    - Reservation lifecycle: PENDING_DEPOSIT → CONFIRMED → IN_HOUSE → CHECKED_OUT → FINISHED
    - FINISHED chỉ khi: checked out + fully paid (không thể FINISHED trực tiếp từ IN_HOUSE)
    - Payment allocation phải link đến cụ thể FolioItem (không chỉ update tổng số)
    - Mỗi payment transaction phải có code unique để track

---

## [DTO Naming Consistency - Merge Create/Update + Response Align]

- **Thời gian:** 07/03/2026
- **Bối cảnh:** Refactor reservation DTO naming sau khi gộp create/update request.
- **Vấn đề:** Dùng tên `CreateBookingRequest`, `BookingResponse` trong khi domain model thống nhất dùng tên
  `Reservation`
  , gây nhầm lẫn cho developer và gây khó khăn khi search/grep codebase. Test helper cũng dùng tên
  `createBookingRequest`
  cũ.
- **Nguyên nhân:** Chưa thống nhất terminology sớm giữa "booking" vs "reservation", DTO legacy dùng từ booking nhưng
  entity/database/business domain dùng reservation.
- **Giải pháp:**
    - Gộp `CreateBookingRequest`/`UpdateBookingRequest` thành `ReservationRequest` khi có ít trường khác biệt
    - Rename `BookingResponse` → `ReservationResponse` để đồng bộ ngữ nghĩa, giữ nguyên response field names (
      `bookingId`/`bookingCode`) để đảm bảo API compatibility với frontend
    - Cập nhật toàn bộ service/api/test import và type references
    - Đổi test helper `createBookingRequest()` → `createReservationRequest()` để tăng tính nhất quán trong test suite
- **Quy tắc mới:**
    - DTO request/response nên thống nhất tên với domain entity (ví dụ: `ReservationRequest` cho entity `Reservation`)
    - Nếu create/update DTO có cấu trúc giống >70%, nên gộp thành một DTO chung
    - Khi rename DTO, giữ nguyên response field names để tránh phá vỡ API contract với client
    - Test helper method nên dùng tên phù hợp với DTO cuối cùng sau refactor

---

## [API Endpoint Naming - Semantic & Unambiguous]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Tách API thành các controller/service theo domain (Reservation, Customer, Room).
- **Vấn đề:** Endpoint `/api/v1/bookings/available-rooms` và `/api/v1/rooms/available` khó phân biệt, nhầm lẫn về chức
  năng.
- **Nguyên nhân:** Chưa cẩn thận trong lựa chọn tên endpoint, dùng từ chung chung.
- **Giải pháp:**
    - Endpoint phải sát nghĩa nhất có thể: `/api/v1/rooms-class/available` thay vì `/api/v1/rooms/available`
    - CRUD operations cơ bản của 1 domain tập trung trong 1 controller/service (ví dụ: ReservationApi CRUD reservations)
    - Tách biệt rõ module theo domain logic: Customer, Room, Reservation, Service...
- **Quy tắc mới:**
    - Trước khi đặt tên endpoint, kiểm tra có endpoint nào tương tự không
    - Endpoint nên dùng danh từ số nhiều + action cụ thể (ví dụ: GET `/api/v1/rooms-class/available`, POST
      `/api/v1/reservations`)
    - Mỗi domain có riêng controller/service, không dồn chức năng vào 1 file

---

## [BaseEnumStringConverter - Store Enum as String]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Chuẩn hóa cách lưu trữ enum trong DB.
- **Vấn đề:** Nếu không dùng converter, enum có thể lưu dưới dạng ordinal (0, 1, 2...) hoặc string, gây khó đọc khi truy
  xuất SQL trực tiếp.
- **Nguyên nhân:** Chưa áp dụng converter chuẩn cho enum fields.
- **Giải pháp:** Sử dụng `BaseEnumStringConverter` (base class chung) cho tất cả enum fields, đảm bảo enum lưu dưới dạng
  string trong DB, dễ đọc và maintain.
- **Quy tắc mới:**
    - Tất cả enum fields trong entity phải khai báo `@Convert(converter = XxxEnumConverter.class)`
    - Converter class kế thừa `BaseEnumStringConverter<EnumType>`
    - Không để enum lưu dưới dạng ordinal

---

## [Unit Testing Best Practices - Mock vs Real Data]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Viết unit test cho ReservationService với 13 test cases comprehensive.
- **Vấn đề:** Ban đầu mock data không khớp với DTO/entity thực tế (CustomerRequest có 5 fields nhưng mock 6 fields,
  CustomerType không có REGULAR enum value, entity lưu type dưới dạng String).
- **Nguyên nhân:** Chưa kiểm tra kỹ structure của DTO/entity trước khi viết test, giả định các field mà không verify.
- **Giải pháp:**
    - Đọc kỹ DTO/entity definition trước khi tạo mock data
    - CustomerRequest: `(customerId, identityCard, fullName, phoneNumber, email)` - không có field type
    - CustomerEntity: type field là `String`, không phải enum
    - CustomerType enum: `ADULT`, `CHILD`, `VIP`, `CORPORATE` - không có `REGULAR`
    - Sử dụng Mockito annotations: `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`
    - Test structure: Given-When-Then pattern với `@DisplayName` rõ ràng
    - Verify interactions với `verify()`, `never()`, `times(n)`
    - Sử dụng `ArgumentCaptor` để verify values được pass vào repository.save()
- **Quy tắc mới:**
    - **Luôn đọc DTO/entity trước khi viết mock data trong test**
    - Unit test phải cover:
        * Happy paths (multiple scenarios)
        * Validation errors (null, empty, invalid)
        * Business exceptions (not found, inactive, insufficient)
        * Edge cases (duplicate, default values, multiple items)
        * Verification (repository calls, saved entity values)
    - Sử dụng AssertJ (`assertThat`) thay vì JUnit assertions để code test rõ ràng hơn
    - Test method naming: `test[MethodName]_[Scenario]_[ExpectedResult]`
    - Mỗi test chỉ test 1 scenario cụ thể, tránh test quá nhiều thứ trong 1 method

---

## [Room Allocation Design - Supporting Multiple Entries with Different People Count]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Hoàn thiện logic tạo reservation room allocation với support cho multiple people per room class.
- **Vấn đề:** Ban đầu chỉ lưu `quantity` (số phòng) nhưng không lưu số người cho mỗi allocation, khiến việc check-in
  không biết người được xếp vào phòng nào.
- **Nguyên nhân:** Thiếu field `numberOfPeople` trong `ReservationRoomAllocationEntity` và `numberOfPeoplePerAllocation`
  trong `RoomClassQuantity`.
- **Giải pháp:**
    - Thêm `numberOfPeoplePerAllocation` vào `RoomClassQuantity` DTO (bổ sung field thứ 3)
    - Thêm `numberOfPeople` field vào `ReservationRoomAllocationEntity` để lưu số người
    - Cập nhật `createRoomAllocations()` để set `numberOfPeople` từ request
    - Format: `RoomClassQuantity(roomClassId, quantity, numberOfPeoplePerAllocation)`
    - Ví dụ: Đặt 2 phòng Standard với 2 người, rồi 1 phòng Standard với 3 người → 2 separate allocations
- **Quy tắc mới:**
    - **Mỗi entry trong `roomClassQuantities` là 1 allocation riêng biệt** → cho phép duplicate room class với số người
      khác nhau
    - Khi response, response vẫn merge quantity theo room class ID (vì frontend chỉ cần tổng số phòng)
    - Khi check-in, staff xem chi tiết allocations để biết: "đặt 2 phòng Standard 2 người + 1 phòng Standard 3 người"
    - Test phải cover 3 parameters: `createRoomClassQuantity(roomClassId, quantity, numberOfPeople)`

---

## [Room Allocation Design - numberOfPeople instead of quantity]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Hoàn thiện logic tạo reservation room allocation.
- **Vấn đề:** Ban đầu dùng field `quantity` nhưng không rõ nghĩa là "số phòng" hay "số người", gây nhầm lẫn. Response
  merge allocations thành Map, làm mất thông tin chi tiết từng allocation.
- **Nguyên nhân:** Tên field không rõ ràng, comment trong DDL gây hiểu nhầm. Response design sai - merge làm mất thông
  tin.
- **Giải pháp:**
    - Đổi hoàn toàn từ `quantity` → `numberOfPeople` trong DDL, entity, DTO
    - `RoomClassQuantity(roomClassId, numberOfPeople)` - chỉ 2 fields
    - Bỏ validation số phòng available (vì numberOfPeople không liên quan trực tiếp)
    - **Response trả về List allocations, KHÔNG merge** - giữ nguyên từng allocation riêng biệt
    - Staff xem chi tiết: "Standard 3 người + Standard 2 người" → quyết định cần bao nhiêu phòng vật lý
- **Quy tắc mới:**
    - `numberOfPeople` = tổng số người, không phải số phòng
    - **Response KHÔNG merge**, trả về `List<RoomClassQuantity> allocations`
    - Mỗi allocation lưu độc lập, staff xem từng entry để xếp phòng vật lý
    - Validation chỉ check room class exists và active
    - Test: `createRoomClassQuantity(roomClassId, numberOfPeople)` - 2 parameters
    - Ví dụ: `[(1L, 3), (1L, 2)]` → 2 allocations riêng, không merge thành `(1L, 5)`

---

## [Deposit Calculation & Payment Transaction Creation]

- **Thời gian:** 05/03/2026
- **Bối cảnh:** Hoàn thiện luồng tạo reservation với tính toán tiền cọc và tạo payment transaction tạm thời.
- **Vấn đề:** Ban đầu chỉ tạo reservation mà không tính deposit và tạo folio/payment, dẫn đến dữ liệu không nhất quán.
- **Nguyên nhân:** Chưa implement logic tính toán deposit dựa trên basePrice, extraPersonFee, và validation capacity.
- **Giải pháp:**
    - Tạo `FolioRepository` và `PaymentTransactionRepository`
    - Tạo enum converters cho `FolioStatus`, `PaymentMethod`, `PaymentTransactionStatus`, `PaymentTransactionType`
    - Apply enum typed cho `FolioEntity` và `PaymentTransactionEntity`
    - Thêm logic tính `totalRoomCost`: `basePrice + (extraPeople * extraPersonFee)` cho mỗi allocation
    - Tính `depositAmount = totalRoomCost * 0.20` (20%)
    - Validate `numberOfPeople <= maxCapacity`, throw `EXCEED_MAX_CAPACITY` nếu vượt
    - Tách orchestration trong `createReservation()`: calculate cost → calculate deposit → save reservation → create
      allocations → create folio → create pending payment
    - Tạo `FolioEntity` với status `OPEN`, balance = depositAmount
    - Tạo `PaymentTransactionEntity` với type `DEPOSIT`, status `PENDING`, method `CASH` (default)
- **Quy tắc mới:**
    - **Deposit = 20% tổng tiền phòng** (có thể config sau)
    - **Mỗi allocation tính riêng**: basePrice + phụ thu nếu vượt standardCapacity
    - **Validation**: numberOfPeople phải <= maxCapacity
    - **Transaction**: Folio + Payment tạo trong cùng transaction với Reservation
    - **Payment code format**: `PT + YYMMDD + 6 random chars`
    - Test phải cover: deposit calculation, extra person fee, exceed max capacity exception

---

## [Compile fail do dùng String thay cho enum typed trong payment]

- **Thời gian:** 07/03/2026
- **Bối cảnh:** Chạy test sau khi thêm luồng cập nhật reservation.
- **Vấn đề:** Build fail ở `PaymentServiceImpl` do gán chuỗi trực tiếp (`"VNPAY"`, `"PENDING"`, `"PAYMENT"`) cho các
  field enum typed; đồng thời thiếu repository method tra cứu theo `transactionReference`.
- **Nguyên nhân:** Refactor enum đã hoàn tất ở entity nhưng service/repository chưa đồng bộ 100%.
- **Giải pháp:** Đổi sang enum typed (`PaymentMethod.VNPAY`, `PaymentTransactionType.PAYMENT`,
  `PaymentTransactionStatus.PENDING/SUCCESS/FAILED`) và thêm `findByTransactionReference(...)` vào
  `PaymentTransactionRepository`.
- **Quy tắc mới:** Sau mỗi lần đổi field sang enum typed ở entity, phải grep toàn service/repository để xóa string
  literal liên quan và bổ sung repository contract cần thiết trước khi chạy test.

---

## [Kiểm thử endpoint update reservation bằng MockMvc + advice]

- **Thời gian:** 07/03/2026
- **Bối cảnh:** Bổ sung kiểm thử cho `PUT /api/v1/reservations/{id}`.
- **Vấn đề:** Nếu chỉ test service thì chưa bảo đảm controller mapping và error contract hoạt động đúng.
- **Nguyên nhân:** Thiếu test tầng API để xác nhận HTTP status + payload lỗi chuẩn.
- **Giải pháp:** Thêm `@WebMvcTest(ReservationApi.class)` + `@Import(GlobalExceptionHandler.class)`, mock
  `ReservationService`, verify cả happy-path và lỗi (`RESERVATION_UPDATE_LOCKED`, `RESERVATION_NOT_FOUND`).
- **Quy tắc mới:** Với API nghiệp vụ quan trọng, luôn có test endpoint tối thiểu cho: 1 happy path + 1 business error +
  1 not found/validation error.

---

## [Gộp DTO create/update khi payload giống nhau]

- **Thời gian:** 07/03/2026
- **Bối cảnh:** Chuẩn hóa API reservation sau khi thêm luồng update.
- **Vấn đề:** Duy trì nhiều DTO request cho cùng payload làm tăng chi phí refactor và dễ lệch field giữa create/update.
- **Nguyên nhân:** Thiết kế theo endpoint thay vì theo shape dữ liệu thực tế.
- **Giải pháp:** Gộp `CreateBookingRequest` và payload update thành một DTO chung `ReservationRequest` dùng cho cả
  `POST` và `PUT`.
- **Quy tắc mới:** Nếu create/update có cùng cấu trúc input thì dùng chung một request DTO; chỉ tách khi nghiệp vụ hoặc
  validation khác biệt rõ ràng.

---

## [Check-in Flow - Assign Physical Room & Update Status]

- **Thời gian:** 08/03/2026
- **Bối cảnh:** Triển khai nghiệp vụ check-in reservation với gán phòng vật lý manual/auto.
- **Vấn đề:** Cần validate business rule chặt chẽ (status CONFIRMED only), gán phòng vật lý cho allocations, tránh trùng
  phòng trong 1 request, và lưu actual check-in time.
- **Nguyên nhân:** Check-in có nhiều edge case: partial assignment, auto-assign khi thiếu, conflict phòng, room class
  mismatch.
- **Giải pháp:**
    - Thêm field `status` (enum `ReservationRoomAllocationStatus`) và `actualCheckIn` vào `ReservationRoomEntity`
    - Tạo DTO: `ReservationCheckInRequest(autoAssign, List<ReservationRoomCheckInRequest(reservationRoomId, roomId)>)`
    - Validate: chỉ check-in khi `ReservationStatus = CONFIRMED`
    - Step assignment:
        1. Parse manual assignments thành Map `reservationRoomId → roomId`
        2. Validate reservationRoomId thuộc reservation hiện tại, không duplicate
        3. Với mỗi allocation: resolve room (manual có trong map → validate, không có + autoAssign=true → tìm AVAILABLE
           room)
        4. Validate phòng: AVAILABLE, active, cùng roomClass, không bị gán trùng trong set `usedRoomIds`
        5. Set allocation: roomEntity, status=CHECKED_IN, actualCheckIn=Instant.now()
        6. Set room: status=OCCUPIED
    - Cập nhật reservation status → IN_HOUSE
    - Mỗi allocation có `actualCheckIn` riêng (mở rộng cho partial check-in sau)
    - Bổ sung ErrorCode: `RESERVATION_CHECKIN_NOT_ALLOWED`, `ROOM_NOT_AVAILABLE`, `ROOM_CLASS_MISMATCH`,
      `RESERVATION_ROOM_ASSIGNMENT_REQUIRED`
    - Thêm repository method: `findByReservationEntity_IdAndIsActiveTrue()` cho allocation,
      `findByRoomClassEntity_IdAndStatusAndIsActiveTrueOrderByIdAsc()` cho room auto-assign
- **Quy tắc mới:**
    - Check-in chỉ cho CONFIRMED status
    - `actualCheckIn` lưu ở tầng `reservation_room` (từng allocation), không lưu ở `reservation`
    - Gán phòng manual: frontend gửi full list (reservationRoomId, roomId), validate từng cặp
    - Auto-assign: query `AVAILABLE` rooms theo roomClass, lọc `usedRoomIds` để tránh trùng
    - Validate đầy đủ: room active, status AVAILABLE, roomClass match, không gán trùng
    - Persist batch: `roomRepository.saveAll()` + `reservationRoomAllocationRepository.saveAll()`
    - Test phải cover: manual success, auto success, reject invalid status, reject room mismatch/not available

---

## [Refactor Naming - ReservationRoomAllocation → ReservationRoom]

- **Thời gian:** 08/03/2026
- **Bối cảnh:** Sau khi đổi entity `ReservationRoomEntity`, còn nhiều chỗ dùng tên cũ `ReservationRoomAllocation` trong
  enum, repository, imports.
- **Vấn đề:** Naming inconsistency gây khó search/refactor sau này, enum/repository không khớp với entity/table name.
- **Nguyên nhân:** Refactor entity nhưng chưa propagate tên mới toàn bộ codebase.
- **Giải pháp:**
    - Rename enum: `ReservationRoomAllocationStatus` → `ReservationRoomStatus`
    - Rename repository: `ReservationRoomAllocationRepository` → `ReservationRoomRepository`
    - Cập nhật imports trong: `ReservationServiceImpl`, `RoomAllocationServiceImpl`, `ServiceBookingServiceImpl`, và tất
      cả test files
    - Sửa field name trong `FolioEntity`: `reservationRoomAllocation` → `reservationRoom`
    - Áp dụng `BaseEnumStringConverter` cho `ReservationRoomStatus`
- **Quy tắc mới:**
    - Sau khi rename entity, **ngay lập tức** rename enum/repository/field liên quan để giữ naming consistency
    - Dùng grep search `ReservationRoomAllocation` để tìm tất cả references cần đổi
    - Sau rename, chạy `mvn clean compile` để verify không breaking changes

---

## [Folio Balance Calculation - Avoid Delta Accumulation]

- **Thời gian:** 08/03/2026
- **Bối cảnh:** Refactor TODO trong `FolioServiceImpl` về cách cập nhật balance khi service charge thay đổi.
- **Vấn đề:** Ban đầu dùng delta-based update (`balance = balance + delta`), dễ bị lỗi tích lũy nếu có nhiều lần update
  hoặc concurrent request.
- **Nguyên nhân:** Tối ưu hóa sớm (premature optimization), nghĩ rằng tính delta nhanh hơn query lại toàn bộ items.
- **Giải pháp:**
    - Tạo method `FolioItemService.calculateTotalCharges(FolioEntity)` để tính tổng từ đầu
    - Thay thế delta logic bằng `recalculateFolioTotals()` trong `updateServiceCharge()` và `cancelServiceCharge()`
    - Công thức: `totalCharges = sum(active FolioItems.totalPrice)`, `balance = totalCharges - totalPaid`
    - Thêm repository method: `findByFolioEntity_IdAndIsActiveTrue()` để lấy active items
- **Quy tắc mới:**
    - **Balance/totals phải tính từ đầu**, không dùng delta accumulation
    - Mỗi lần update service charge → recalculate full totals từ database
    - Trade-off: query nhiều hơn nhưng đảm bảo accuracy 100%, tránh race condition
    - Apply pattern này cho mọi aggregate totals (payment, folio, invoice...)

---

## [OneToOne vs OneToMany - Service Booking & Folio Item]

- **Thời gian:** 08/03/2026
- **Bối cảnh:** Fix TODO trong `ServiceBookingEntity` để sửa relationship với `FolioItemEntity`.
- **Vấn đề:** Ban đầu để `OneToMany` trong khi nghiệp vụ chỉ cần 1 folio item cho 1 service booking.
- **Nguyên nhân:** Thiết kế entity không bám sát business rule "1 service booking = 1 charge item".
- **Giải pháp:**
    - Xóa `@OneToOne(mappedBy = "serviceBookingEntity")` khỏi `ServiceBookingEntity` (không cần navigation từ
      ServiceBooking → FolioItem)
    - Giữ `@OneToOne` ở `FolioItemEntity` (owning side), vì query luôn đi từ FolioItem → ServiceBooking
    - Service layer dùng `findByServiceBookingEntityAndIsActiveTrue()` để tìm folio item từ service booking
- **Quy tắc mới:**
    - Không cần bidirectional mapping nếu chỉ query theo 1 chiều
    - Owning side (có `@JoinColumn`) nên ở entity có nhiều queries hơn
    - OneToOne chỉ cần mappedBy khi cần navigation 2 chiều, còn lại owning side đủ

---

## [API mark as paid offline: chỉ cần gọi service, không cần custom logic controller]

- **Thời gian:** 11/03/2026
- **Bối cảnh:** Thêm API cho phép lễ tân đánh dấu thanh toán offline (CASH) đã hoàn thành.
- **Vấn đề:** Ban đầu định viết nhiều logic ở controller, nhưng thực tế chỉ cần gọi service markAsPaid đã đủ kiểm soát nghiệp vụ và validation.
- **Nguyên nhân:** Service đã kiểm tra trạng thái, phương thức, và cập nhật liên quan. Controller chỉ cần expose endpoint đúng chuẩn REST.
- **Giải pháp:** Controller chỉ nhận request, gọi service, trả response. Không lặp lại validation ở controller.
- **Quy tắc mới:** Nếu service đã kiểm soát đầy đủ nghiệp vụ và validation, controller chỉ nên làm nhiệm vụ expose endpoint, không lặp lại logic.

---

*Lưu ý cho AI: Đọc kỹ các bài học này để tránh đề xuất các giải pháp đã từng gây lỗi trong quá khứ.*