package com.product.hms.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
}
