package com.product.hms.api;

import com.product.hms.dto.response.RoomImgResponse;
import com.product.hms.service.RoomImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/room-images")
@RequiredArgsConstructor
public class RoomImgApi {

    private final RoomImgService roomImgService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomImgResponse> uploadImage(
            @RequestParam Long roomClassId,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary
    ) throws IOException {
        RoomImgResponse response = roomImgService.uploadImage(roomClassId, file, isPrimary);
        return ResponseEntity.ok(response);
    }
}
