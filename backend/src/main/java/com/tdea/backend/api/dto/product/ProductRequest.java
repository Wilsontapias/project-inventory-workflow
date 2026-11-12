package com.tdea.backend.api.dto.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/** Datos validados para crear o actualizar un producto del catálogo. */
public record ProductRequest(
        @NotBlank @Size(max = 40) String sku,
        @NotBlank @Size(max = 180) String name,
        @NotBlank @Size(max = 100) String brand,
        @Size(max = 100) String model,
        @NotNull ProductCategory category,
        @Size(max = 100) String subcategory,
        @NotNull @DecimalMin("0.0") BigDecimal unitCost,
        @NotNull @DecimalMin("0.0") BigDecimal salePrice,
        @NotNull @Min(0) Integer stockCurrent,
        @NotNull @Min(0) Integer stockMinimum,
        @NotNull @Min(0) Integer stockIdeal,
        @NotNull @Min(0) Integer daysInInventory,
        @NotNull @DecimalMin("0.0") BigDecimal averageMonthlyDemand,
        @PastOrPresent LocalDate lastSaleDate,
        @NotNull Long preferredSupplierId) {
}
