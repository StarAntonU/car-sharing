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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import star.carsharing.dto.rental.CreateRentalRequestDto;
import star.carsharing.dto.rental.RentalResponseDto;
import star.carsharing.dto.rental.RentalResponseWithActualReturnDateDto;
import star.carsharing.dto.rental.UserRentalIsActiveRequestDto;
import star.carsharing.exception.checked.NotificationException;
import star.carsharing.model.User;
import star.carsharing.service.RentalService;

@Tag(name = "Rental", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a rental", description = "Create a new rental")
    public RentalResponseDto createRental(
            Authentication authentication, @RequestBody @Valid CreateRentalRequestDto requestDto)
            throws NotificationException {
        return rentalService.createRental(authentication, requestDto);
    }

    @GetMapping("/{rentalId}")
    @Operation(summary = "View the rental", description = "View the rental by id")
    public RentalResponseDto getRentalById(
            Authentication authentication, @PathVariable Long rentalId) {
        return rentalService.getRentalById(getUserId(authentication), rentalId);
    }

    @PostMapping("/{rentalId}/return")
    @Operation(summary = "Close the rental", description = "Close the rental by id")
    public RentalResponseWithActualReturnDateDto closeRental(
            Authentication authentication, @PathVariable Long rentalId) {
        return rentalService.closeRental(getUserId(authentication), rentalId);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "View user`s rentals",
            description = "View the user`s rentals active or inactive")
    public Page<RentalResponseDto> getUserRentalIsActive(
            @RequestBody @Valid UserRentalIsActiveRequestDto requestDto, Pageable pageable) {
        return rentalService.getUserRentalIsActive(requestDto, pageable);
    }

    private Long getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}

