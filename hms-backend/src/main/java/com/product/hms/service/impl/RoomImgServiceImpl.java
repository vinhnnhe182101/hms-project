package com.product.hms.service.impl;

import com.product.hms.dto.response.RoomImgResponse;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.entity.RoomImgEntity;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.repository.RoomImgRepository;
import com.product.hms.service.RoomImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RoomImgServiceImpl implements RoomImgService {

    private final RoomImgRepository roomImgRepository;
    private final RoomClassRepository roomClassRepository;

    @Override
    @Transactional
    public RoomImgResponse uploadImage(Long roomClassId, MultipartFile file, boolean isPrimary) throws IOException {
        // Kiểm tra RoomClass tồn tại
        RoomClassEntity roomClass = roomClassRepository.findById(roomClassId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + roomClassId));

        // Nếu ảnh mới là primary, bỏ primary của ảnh cũ trước
        if (isPrimary) {
            roomImgRepository.findFirstByRoomClassEntityIdAndIsPrimaryTrue(roomClassId)
                    .ifPresent(oldPrimary -> {
                        oldPrimary.setIsPrimary(false);
                        roomImgRepository.save(oldPrimary);
                    });
        }

        // Tạo entity và lưu ảnh
        RoomImgEntity imgEntity = new RoomImgEntity();
        imgEntity.setRoomClassEntity(roomClass);
        imgEntity.setImgData(file.getBytes());          // Lưu binary vào LONGBLOB
        imgEntity.setImgType(file.getContentType());    // VD: "image/jpeg"
        imgEntity.setIsPrimary(isPrimary);

        RoomImgEntity saved = roomImgRepository.save(imgEntity);

        // Convert sang Base64 Data URL để trả về client
        String base64 = Base64.getEncoder().encodeToString(saved.getImgData());
        String dataUrl = "data:" + saved.getImgType() + ";base64," + base64;

        return RoomImgResponse.builder()
                .id(saved.getId())
                .dataUrl(dataUrl)
                .imgType(saved.getImgType())
                .isPrimary(saved.getIsPrimary())
                .build();
    }
}
