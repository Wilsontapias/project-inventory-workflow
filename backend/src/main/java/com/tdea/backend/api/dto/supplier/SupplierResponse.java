package com.tdea.backend.api.dto.supplier;

import java.math.BigDecimal;

import com.tdea.backend.domain.enums.SupplierCategory;

/** Representación REST de los datos comerciales de un proveedor. */
public record SupplierResponse(
        Long id,
        String taxId,
        String businessName,
        String city,
        String address,
        String billingEmail,
        String phone,
        String contactName,
        SupplierCategory category,
        Integer paymentTermDays,
        Integer leadTimeBusinessDays,
        BigDecimal creditLimit,
        BigDecimal creditUsed,
        BigDecimal availableCredit,
        BigDecimal performanceScore,
        String representedBrands) {
}
