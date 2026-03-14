
package com.product.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskCountResponse {
    private long scheduled;
    private long inProgress;
    private long completed;
    private long total;
    private double completionRate;

    public static TaskCountResponse of(long scheduled, long inProgress, long completed) {
        long total = scheduled + inProgress + completed;
        double rate = total > 0 ? Math.round((completed * 100.0 / total) * 10) / 10.0 : 0;

        return TaskCountResponse.builder()
                .scheduled(scheduled)
                .inProgress(inProgress)
                .completed(completed)
                .total(total)
                .completionRate(rate)
                .build();
    }
}