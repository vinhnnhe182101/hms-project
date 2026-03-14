// service/housekeeping/DamageService.java
package com.product.hms.service.housekeeping;

import com.product.hms.dto.request.DamageReportRequest;
import com.product.hms.dto.response.DamageReportResponse;
import java.util.List;

public interface DamageService {
    DamageReportResponse reportDamage(DamageReportRequest request);
    List<DamageReportResponse> getMyDamageReports();
    DamageReportResponse resolveDamage(Long reportId);
}