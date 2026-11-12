package com.tdea.backend.api.dto.dashboard;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Indicadores calculados con el inventario y proveedores cargados en H2. */
public record DashboardSummaryResponse(
        long productCount,
        long supplierCount,
        long criticalProductCount,
        long productsOlderThan90Days,
        long lowRotationProductCount,
        BigDecimal inventoryCostValueCop,
        BigDecimal agingInventoryCostValueCop,
        BigDecimal suggestedReorderValueCop,
        OffsetDateTime generatedAt,
        String financialDataNote) {
}
