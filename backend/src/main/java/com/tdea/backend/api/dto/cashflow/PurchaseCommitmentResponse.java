package com.tdea.backend.api.dto.cashflow;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.CommitmentStatus;

/** Compromiso de pago y proveedor asociado, si existe. */
public record PurchaseCommitmentResponse(
        Long id,
        LocalDate dueDate,
        String description,
        BigDecimal amount,
        CommitmentStatus status,
        Long supplierId,
        String supplierName) {
}
