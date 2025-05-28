package star.carsharing.dto.payment;

public record PaymentResponseDto(
        String sessionId,
        String sessionUrl
) {
}
