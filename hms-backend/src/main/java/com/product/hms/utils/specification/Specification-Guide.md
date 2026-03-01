# Hướng dẫn sử dụng Search & Sort Specification (Doctor API Example)

## Tổng quan

Hệ thống Specification cho phép xây dựng các truy vấn động, mạnh mẽ, hỗ trợ cả tìm kiếm (search), sắp xếp (sort), phân
trang (pagination) và các phép toán tổng hợp (aggregate) trên Spring Data JPA.

## 1. Các thành phần chính

### a. SearchCriteria

- Đại diện cho một điều kiện tìm kiếm.
- Thuộc tính: `fieldName`, `comparisonOperator`, `comparedValue`, `joinType` (LEFT/INNER...)

### b. SortCriteria

- Đại diện cho một điều kiện sắp xếp.
- Thuộc tính: `fieldName`, `aggregationFunction` (NONE, AVG, COUNT...), `sortDirection` (ASC/DESC), `joinType`.

### c. SpecificationUtils

- Hỗ trợ build Specification từ danh sách SearchCriteria và SortCriteria, cho phép gộp cả search và sort vào một
  Specification duy nhất.

## 2. Cách truyền fieldName cho SearchCriteria/SortCriteria

Bạn có thể truyền fieldName theo 2 cách:

### a. Cách cũ (truyền string thủ công)

```java
new SearchCriteria("staffEntity.fullName",ComparisonOperator.CONTAINS, value, JoinType.LEFT)
new

SortCriteria("staffEntity.reviewEntities.rating",AggregationFunction.AVG, sortDirection, JoinType.LEFT)
```

### b. Cách mới (gợi ý tên trường, an toàn, dễ refactor)

- Sử dụng @FieldNameConstants trên entity để sinh constant cho từng field.
- Sử dụng FieldNameUtils.joinFields để build fieldName dạng "a.b.c" an toàn.

Ví dụ:

```java
// DoctorEntity có @FieldNameConstants
FieldNameUtils.joinFields(
        DoctorEntity.Fields.staffEntity,
        StaffEntity.Fields.fullName
        )
// Kết quả: "staffEntity.fullName"

FieldNameUtils.

joinFields(
        DoctorEntity.Fields.staffEntity,
        StaffEntity.Fields.reviewEntities,
        ReviewEntity.Fields.rating
        )
// Kết quả: "staffEntity.reviewEntities.rating"

// Áp dụng cho SearchCriteria/SortCriteria
new

SearchCriteria(
        FieldNameUtils.joinFields(
                DoctorEntity.Fields.staffEntity,
        StaffEntity.Fields.fullName
        ),

ComparisonOperator.CONTAINS,
value,
JoinType.LEFT
)
```

- Ưu điểm: IDE sẽ gợi ý tên field, tránh lỗi chính tả, dễ refactor khi đổi tên field trong entity.
- Cách cũ (string thủ công) vẫn hoạt động bình thường nếu bạn muốn dùng nhanh.

## 3. Repository

Repository phải extend `JpaSpecificationExecutor<Entity>` để sử dụng Specification:

```java
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long>, JpaSpecificationExecutor<DoctorEntity> {
}
```

## 4. Service: Xây dựng search + sort + phân trang

### a. Cách 1: Dùng repository.findAll (dễ dùng, phù hợp join đơn giản)

```java
public Page<DoctorResponse> getDoctors(DoctorDTO doctorDTO, int index, int size) {
    Pageable pageable = pageUtils.getPageable(index, size);
    List<SearchCriteria> searchCriterias = ...
    List<SortCriteria> sortCriterias = ...
    Page<DoctorEntity> doctorEntityPage = doctorRepository.findAll(
            specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable
    );
    return doctorEntityPage.map(doctorConverter::toResponse);

```

### b. Cách 2: Dùng PageSpecificationUtils (xử lý join phức tạp, count distinct, custom count)

```java
public Page<DoctorResponse> getDoctors(DoctorDTO doctorDTO, int index, int size) {
    Pageable pageable = pageUtils.getPageable(index, size);
    List<SearchCriteria> searchCriterias = ...
    List<SortCriteria> sortCriterias = ...
    Page<DoctorEntity> doctorEntityPage = pageSpecificationUtils.getPage(
            specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias),
            pageable,
            DoctorEntity.class,
            true // true nếu cần count distinct (ví dụ join nhiều bảng)
    );
    return doctorEntityPage.map(doctorConverter::toResponse);

```

## 5. Controller: Nhận search + sort từ DTO, trả về kết quả phân trang

```java

@GetMapping("/page/{pageIndex}")
public ResponseEntity<Map<String, Object>> getAllDoctors(@PathVariable int pageIndex, @ModelAttribute DoctorDTO doctorDTO) {
    Page<DoctorResponse> doctorResponsePage = doctorService.getDoctors(doctorDTO, pageIndex, PAGE_SIZE_FOR_LIST);
    return ResponseEntity.ok(
            Map.of(
                    "doctors", doctorResponsePage.getContent(),
                    "totalPages", doctorResponsePage.getTotalPages(),
                    "currentPage", doctorResponsePage.getNumber()
            )
    );
}
```

## 6. DTO mẫu (DoctorDTO)

```java
public class DoctorDTO {
    private String staffEntityFullName;
    private Long staffEntityDepartmentEntityId;
    private Double minStarRating;
    private String sortFieldName; // ví dụ: staffEntity.fullName, staffEntity.reviewEntities.rating
    private AggregationFunction sortDirection; // ASC hoặc DESC
    // ... các trường khác
}
```

## 7. Hướng dẫn sử dụng cho entity khác

- Tạo DTO tương tự DoctorDTO cho entity cần search/sort.
- Tạo SearchCriteria và SortCriteria phù hợp (có thể dùng FieldNameUtils.joinFields hoặc string thủ công).
- Gọi repository.findAll hoặc pageSpecificationUtils.getPage như ví dụ trên.

## 8. Lưu ý

- Các trường search/sort phải đúng tên mapping trong entity.
- Có thể kết hợp nhiều điều kiện search và nhiều sort.
- Hỗ trợ cả aggregate sort (AVG, COUNT, SUM, ...).
- Nên validate input DTO trước khi build criteria.
- Pagination nên dùng để tránh trả về quá nhiều kết quả.
- Nếu join phức tạp hoặc cần count distinct, nên dùng PageSpecificationUtils.
- **Nên ưu tiên cách mới với FieldNameUtils và @FieldNameConstants để code an toàn, dễ bảo trì.**

## 9. Ví dụ gọi API

```
GET /api/doctor/page/0?staffEntityFullName=Nguyen&sortFieldName=staffEntity.reviewEntities.rating&sortDirection=DESC
```

## 10. Best Practice

- Tách logic build search/sort ra service/utils.
- Luôn kiểm tra null cho các trường search/sort.
- Sử dụng Enum cho sortFieldName, sortDirection ở phía FE để tránh lỗi chính tả.
- Có thể mở rộng thêm các phép toán so sánh/tổng hợp nếu cần.

---

**Hệ thống search & sort này giúp API linh hoạt, dễ mở rộng, dễ bảo trì và tối ưu cho các use-case thực tế!**
