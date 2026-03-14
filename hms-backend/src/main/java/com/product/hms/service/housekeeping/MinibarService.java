// service/housekeeping/MinibarService.java
package com.product.hms.service.housekeeping;

import com.product.hms.dto.request.MinibarConsumptionRequest;
import com.product.hms.dto.response.MinibarConsumptionResponse;
import com.product.hms.dto.response.MinibarItemResponse;
import java.util.List;

public interface MinibarService {
    List<MinibarItemResponse> getRoomMinibarItems(Long roomId);
    List<MinibarConsumptionResponse> reportConsumption(MinibarConsumptionRequest request);
    List<MinibarConsumptionResponse> getConsumptionHistory(Long reservationId);
}