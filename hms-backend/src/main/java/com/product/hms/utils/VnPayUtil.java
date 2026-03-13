package com.product.hms.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VnPayUtil {

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url}")
    private String vnpUrl;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String generatePaymentUrl(String txnRef,
            long amountVnd,
            String ipAddress,
            String orderInfo,
            String returnUrl) {
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amountVnd * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_IpAddr", ipAddress);
        vnpParams.put("vnp_ReturnUrl", returnUrl);

        String createDate = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        vnpParams.put("vnp_CreateDate", createDate);

        String expiryDate = LocalDateTime.now().plusMinutes(15).format(DATE_TIME_FORMATTER);
        vnpParams.put("vnp_ExpiryDate", expiryDate);

        return buildUrlWithSignature(vnpParams);
    }

    public boolean validateSignature(Map<String, String> queryParams) {
        String receivedHash = queryParams.get("vnp_SecureHash");
        if (receivedHash == null || vnpHashSecret == null || vnpHashSecret.isEmpty()) {
            return false;
        }

        Map<String, String> paramsToSign = new HashMap<>(queryParams);
        paramsToSign.remove("vnp_SecureHash");
        paramsToSign.remove("vnp_SecureHashType");

        String data = buildDataToSign(paramsToSign);
        String calculatedHash = hmacSHA512(vnpHashSecret, data);
        return receivedHash.equalsIgnoreCase(calculatedHash);
    }

    private String buildUrlWithSignature(Map<String, String> params) {
        String query = buildQuery(params);
        String dataToSign = buildDataToSign(params);
        String secureHash = hmacSHA512(vnpHashSecret, dataToSign);

        StringBuilder sb = new StringBuilder(vnpUrl);
        if (!vnpUrl.contains("?")) {
            sb.append("?");
        } else if (!vnpUrl.endsWith("&")) {
            sb.append("&");
        }

        sb.append(query)
                .append("&vnp_SecureHash=")
                .append(secureHash);

        return sb.toString();
    }

    private String buildQuery(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder sb = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(urlEncode(fieldName))
                        .append("=")
                        .append(urlEncode(fieldValue));
            }
        }
        return sb.toString();
    }

    private String buildDataToSign(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder sb = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(fieldName)
                        .append("=")
                        .append(urlEncode(fieldValue));
            }
        }
        return sb.toString();
    }

    public String hmacSHA512(String key, String data) {
        try {
            if (key == null || key.isBlank() || data == null) {
                return "";
            }
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte aByte : bytes) {
                String hex = Integer.toHexString(0xff & aByte);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating HMAC SHA512", e);
        }
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return "";
        }
    }
}
