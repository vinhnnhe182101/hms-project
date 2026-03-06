package com.product.hms.repository;

import com.product.hms.entity.RoomImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomImgRepository extends JpaRepository<RoomImgEntity, Long> {

    /** Lấy ảnh primary đầu tiên — dùng cho trang list */
    Optional<RoomImgEntity> findFirstByRoomClassEntityIdAndIsPrimaryTrue(Long roomClassId);

    /**
     * Lấy TẤT CẢ ảnh của một RoomClass — dùng cho trang detail.
     * Sắp xếp: ảnh primary (isPrimary=true) lên đầu, còn lại theo id tăng dần.
     */
    List<RoomImgEntity> findAllByRoomClassEntityIdOrderByIsPrimaryDescIdAsc(Long roomClassId);
}

