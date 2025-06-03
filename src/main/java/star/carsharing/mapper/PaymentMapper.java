package star.carsharing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import star.carsharing.config.MapperConfig;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;
import star.carsharing.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toResponseDto(Payment payment);

    @Mapping(target = "rentalId", source = "rental.id")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "rental.id", source = "rentalId")
    Payment toModel(PaymentRequestDto dto);
}
