package com.tdea.backend.api.dto.cashflow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Resumen financiero demostrativo, calculado desde movimientos y compromisos en H2. */
public record CashFlowSummaryResponse(
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal openingBalanceCop,
        BigDecimal cashBalanceCop,
        BigDecimal periodIncomeCop,
        BigDecimal periodExpensesCop,
        BigDecimal periodNetFlowCop,
        BigDecimal commitmentsDueNext30DaysCop,
        BigDecimal availableForReorderCop,
        BigDecimal suggestedReorderValueCop,
        boolean suggestedReorderWithinCapacity,
        OffsetDateTime generatedAt,
        String dataNote) {
}
