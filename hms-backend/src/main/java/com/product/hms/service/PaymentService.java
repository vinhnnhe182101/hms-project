package com.product.hms.service;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.entity.FolioEntity;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {

    /**
     * Tạo URL thanh toán VnPay mới dựa trên folioId và số tiền cần thanh toán.
     *
     * @param folioId  ID của folio mà khách hàng muốn thanh toán, sẽ được sử dụng để tạo một giao dịch thanh toán mới.
     * @param amount   Số tiền cần thanh toán, sẽ được lưu trong giao dịch thanh toán mới và sử dụng để tạo URL thanh toán VnPay.
     * @param clientIp địa chỉ IP của khách hàng, cần thiết để tạo URL thanh toán VnPay.
     * @return URL thanh toán VnPay mà khách hàng sẽ được chuyển hướng đến để hoàn tất giao dịch.
     */
    String createVnPayPaymentUrl(Long folioId,
                                 BigDecimal amount,
                                 String clientIp);

    /**
     * Tạo URL thanh toán VnPay dựa trên paymentTransactionId đã tồn tại.
     *
     * @param paymentTransactionId ID của giao dịch thanh toán đã được tạo trước đó, chứa thông tin về số tiền
     * @param clientIp             địa chỉ IP của khách hàng, cần thiết để tạo URL thanh toán VnPay
     * @return URL thanh toán VnPay mà khách hàng sẽ được chuyển hướng đến để hoàn tất giao dịch
     */
    String createVnPaymentUrlByPaymentTransactionId(long paymentTransactionId,
                                                    String clientIp);

    /**
     * Xử lý IPN (Instant Payment Notification) từ VnPay sau khi khách hàng hoàn tất thanh toán.
     *
     * @param params các tham số được VnPay gửi về trong IPN, bao gồm thông tin giao dịch và trạng thái thanh toán
     * @return một Map chứa các thông tin cần thiết để cập nhật trạng thái thanh toán trong hệ thống
     */
    Map<String, String> processVnPayIpn(Map<String, String> params);

    /**
     * Xử lý thanh toán cho một folio.
     * Tạo giao dịch thanh toán và phân bổ thanh toán cho các mục folio chưa thanh toán.
     * Cập nhật số dư folio và đánh dấu các mục là PAID khi đã thanh toán đầy đủ.
     *
     * @param folio   folio cần thanh toán
     * @param request chi tiết thanh toán
     * @return PaymentResponse với thông tin thanh toán và số dư còn lại
     */
    PaymentResponse processPaymentForFolio(FolioEntity folio, PaymentRequest request);
}
