package com.tdea.backend.api.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.tdea.backend.api.dto.product.ProductResponse;
import com.tdea.backend.domain.entity.Product;

/** Convierte productos del dominio a DTOs de respuesta. */
@Component
public class ProductMapper {

    /**
     * Convierte una entidad de producto en su representación REST.
     *
     * @param product entidad persistida
     * @return DTO con estado y métricas derivadas del inventario
     */
    public ProductResponse toResponse(Product product) {
        int unitsToReorder = Math.max(0, product.getStockIdeal() - product.getStockCurrent());
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getBrand(),
                product.getModel(),
                product.getCategory(),
                product.getSubcategory(),
                product.getUnitCost(),
                product.getSalePrice(),
                product.getStockCurrent(),
                product.getStockMinimum(),
                product.getStockIdeal(),
                product.getDaysInInventory(),
                product.getAverageMonthlyDemand(),
                product.getLastSaleDate(),
                product.isLowRotation(LocalDate.now()),
                product.calculateStatus(),
                unitsToReorder,
                product.calculateInventoryValue(),
                product.getPreferredSupplier().getId(),
                product.getPreferredSupplier().getBusinessName());
    }
}
