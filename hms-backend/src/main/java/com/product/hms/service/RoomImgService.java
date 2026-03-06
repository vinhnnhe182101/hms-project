package com.product.hms.service;

import com.product.hms.dto.response.RoomImgResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RoomImgService {
    RoomImgResponse uploadImage(Long roomClassId, MultipartFile file, boolean isPrimary) throws IOException;
}
