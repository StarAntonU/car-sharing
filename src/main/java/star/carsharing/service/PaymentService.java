package star.carsharing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createSession(Long userId, PaymentRequestDto requestDto);

    PaymentDto getPaymentById(Long id);

    Page<PaymentDto> getPayments(Long userId, Pageable pageable);

    void paymentSuccess(String sessionId);

    void paymentCancel(String sessionId);
}
