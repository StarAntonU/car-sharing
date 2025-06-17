package star.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import star.carsharing.dto.payment.PaymentDto;
import star.carsharing.dto.payment.PaymentRequestDto;
import star.carsharing.dto.payment.PaymentResponseDto;
import star.carsharing.model.User;
import star.carsharing.service.PaymentService;

@Tag(name = "Payment", description = "Endpoints for managing payments")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    @Operation(summary = "Create a session", description = "Create a session to payment rental")
    public PaymentResponseDto createSession(Authentication authentication,
                                            @RequestBody @Valid PaymentRequestDto requestDto) {
        return paymentService.createSession(getUserId(authentication), requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{paymentId}")
    @Operation(summary = "View the payment", description = "View the payment by id")
    public PaymentDto getById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping
    @Operation(summary = "View user`s payments", description = "View all user`s payments")
    public Page<PaymentDto> getPayments(Authentication authentication, Pageable pageable) {
        return paymentService.getPayments(getUserId(authentication), pageable);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/success")
    @Operation(summary = "View success", description = "View if the payment was success")
    public void paymentSuccess(@RequestParam("session_id") String sessionId) {
        paymentService.paymentSuccess(sessionId);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/cancel")
    @Operation(summary = "View cancel", description = "View if the payment was cancel")
    public void paymentCancel(@RequestParam("session_id") String sessionId) {
        paymentService.paymentCancel(sessionId);
    }

    private Long getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
