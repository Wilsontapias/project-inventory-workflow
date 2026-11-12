package com.tdea.backend.api.dto.cashflow;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.CashMovementType;

/** Movimiento de caja devuelto por la API. */
public record CashMovementResponse(
        Long id,
        LocalDate movementDate,
        CashMovementType type,
        String description,
        String category,
        BigDecimal amount) {
}
