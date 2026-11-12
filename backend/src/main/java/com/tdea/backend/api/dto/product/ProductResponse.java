package com.tdea.backend.api.dto.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.ProductCategory;
import com.tdea.backend.domain.enums.ProductStatus;

/** Representación REST de un producto, sin exponer la entidad JPA. */
public record ProductResponse(
        Long id,
        String sku,
        String name,
        String brand,
        String model,
        ProductCategory category,
        String subcategory,
        BigDecimal unitCost,
        BigDecimal salePrice,
        Integer stockCurrent,
        Integer stockMinimum,
        Integer stockIdeal,
        Integer daysInInventory,
        BigDecimal averageMonthlyDemand,
        LocalDate lastSaleDate,
        boolean lowRotation,
        ProductStatus status,
        Integer unitsToReorder,
        BigDecimal inventoryValue,
        Long preferredSupplierId,
        String preferredSupplierName) {
}
