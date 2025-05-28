package star.carsharing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;
import star.carsharing.exception.checked.NotificationException;

public interface PaymentService {
    PaymentResponseDto createSession(Long userId, PaymentRequestDto requestDto);

    PaymentDto getPaymentById(Long id);

    Page<PaymentDto> getPayments(Long userId, Pageable pageable);

    void paymentSuccess(String sessionId) throws NotificationException;

    void paymentCancel(String sessionId) throws NotificationException;
}
