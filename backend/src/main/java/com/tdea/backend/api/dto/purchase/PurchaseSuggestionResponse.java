package com.tdea.backend.api.dto.purchase;

import java.math.BigDecimal;
import java.util.List;

import com.tdea.backend.api.dto.supplier.SupplierOfferResponse;

/** Recomendación calculada para reabastecer un producto crítico. */
public record PurchaseSuggestionResponse(
        Long productId,
        String sku,
        String productName,
        Integer stockCurrent,
        Integer stockMinimum,
        Integer stockIdeal,
        Integer suggestedQuantity,
        BigDecimal estimatedTotal,
        Long recommendedSupplierId,
        String recommendedSupplierName,
        String justification,
        List<SupplierOfferResponse> supplierAlternatives) {
}
