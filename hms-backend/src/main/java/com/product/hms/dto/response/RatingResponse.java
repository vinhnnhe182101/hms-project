package com.product.hms.dto.response;

import lombok.Data;

@Data
public class RatingResponse {
    private Long id;
    private String name;
    private String avatar;
    private Integer rating;
    private String date;
    private String comment;
}
