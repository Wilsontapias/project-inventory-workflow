package com.tdea.backend.api.dto.cashflow;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos validados para registrar un compromiso de pago futuro. */
public record PurchaseCommitmentRequest(
        @NotNull LocalDate dueDate,
        @NotBlank @Size(max = 160) String description,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        Long supplierId) {
}
