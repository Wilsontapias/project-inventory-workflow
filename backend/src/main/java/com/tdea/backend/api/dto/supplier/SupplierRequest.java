package com.tdea.backend.api.dto.supplier;

import java.math.BigDecimal;

import com.tdea.backend.domain.enums.SupplierCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos validados para crear o actualizar un proveedor. */
public record SupplierRequest(
        @NotBlank @Size(max = 30) String taxId,
        @NotBlank @Size(max = 160) String businessName,
        @NotBlank @Size(max = 80) String city,
        @Size(max = 180) String address,
        @Email @Size(max = 140) String billingEmail,
        @Size(max = 40) String phone,
        @Size(max = 120) String contactName,
        @NotNull SupplierCategory category,
        @NotNull @Min(0) Integer paymentTermDays,
        @NotNull @Min(1) Integer leadTimeBusinessDays,
        @NotNull @DecimalMin("0.0") BigDecimal creditLimit,
        @NotNull @DecimalMin("0.0") BigDecimal creditUsed,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal performanceScore,
        @Size(max = 300) String representedBrands) {
}
