package com.tdea.backend.api.dto.supplier;

import java.math.BigDecimal;

/** Oferta de proveedor expuesta para comparación y recomendación de compra. */
public record SupplierOfferResponse(
        Long id,
        Long supplierId,
        String supplierName,
        BigDecimal unitCost,
        Integer availableStock,
        Integer leadTimeDays,
        Integer paymentTermDays,
        BigDecimal estimatedTotal) {
}
