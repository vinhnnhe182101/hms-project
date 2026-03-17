// dto/response/ReviewResponse.java
package com.product.hms.dto.response;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private String bookingCode;
    private String roomNumber;
    private String roomType;
    private Integer rating;
    private String comment;
    private Timestamp createdAt;
    private Boolean isPublic;
    
    public String getFormattedDate() {
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(createdAt);
    }
}