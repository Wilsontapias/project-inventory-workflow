package com.tdea.backend.api.dto.cashflow;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.CashMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/** Datos validados para registrar un ingreso o gasto de demostración. */
public record CashMovementRequest(
        @NotNull @PastOrPresent LocalDate movementDate,
        @NotNull CashMovementType type,
        @NotBlank @Size(max = 160) String description,
        @NotBlank @Size(max = 80) String category,
        @NotNull @DecimalMin("0.01") BigDecimal amount) {
}
