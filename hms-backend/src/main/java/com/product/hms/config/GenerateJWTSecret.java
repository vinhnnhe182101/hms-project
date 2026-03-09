package com.product.hms.config;

import java.security.SecureRandom;
import java.util.Base64;

public class GenerateJWTSecret {
    public static void main(String[] args) {
        // Tạo secret key 512-bit (64 bytes) - rất an toàn
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[64];
        secureRandom.nextBytes(keyBytes);
        
        // Encode thành Base64 URL-safe
        String secretKey = Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
        
        System.out.println("JWT Secret Key (512-bit):");
        System.out.println(secretKey);
        System.out.println("\nĐộ dài: " + secretKey.length() + " ký tự");
    }
}