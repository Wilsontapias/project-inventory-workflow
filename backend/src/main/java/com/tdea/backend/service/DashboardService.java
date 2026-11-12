package com.tdea.backend.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tdea.backend.api.dto.dashboard.DashboardSummaryResponse;
import com.tdea.backend.domain.entity.Product;
import com.tdea.backend.repository.ProductRepository;
import com.tdea.backend.repository.SupplierRepository;

/** Calcula indicadores agregados para el dashboard de inventario. */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseSuggestionService purchaseSuggestionService;

    /**
     * Construye el servicio del dashboard.
     *
     * @param productRepository persistencia de productos
     * @param supplierRepository persistencia de proveedores
     * @param purchaseSuggestionService cálculo de sugerencias de compra
     */
    public DashboardService(ProductRepository productRepository, SupplierRepository supplierRepository,
            PurchaseSuggestionService purchaseSuggestionService) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.purchaseSuggestionService = purchaseSuggestionService;
    }

    /**
     * Construye el resumen de inventario y reabastecimiento.
     *
     * @return métricas derivadas de los datos ficticios en H2
     */
    public DashboardSummaryResponse getSummary() {
        List<Product> products = productRepository.findAll();
        BigDecimal inventoryValue = products.stream()
                .map(Product::calculateInventoryValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal agingInventoryValue = products.stream()
                .filter(product -> product.getDaysInInventory() > 90)
                .map(Product::calculateInventoryValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long criticalCount = products.stream()
                .filter(product -> product.getStockCurrent() <= product.getStockMinimum())
                .count();
        long agingCount = products.stream()
                .filter(product -> product.getDaysInInventory() > 90)
                .count();
        long lowRotationCount = products.stream()
                .filter(product -> product.isLowRotation(java.time.LocalDate.now()))
                .count();

        return new DashboardSummaryResponse(
                products.size(),
                supplierRepository.count(),
                criticalCount,
                agingCount,
                lowRotationCount,
                inventoryValue,
                agingInventoryValue,
                purchaseSuggestionService.totalSuggestedReorderValue(),
                OffsetDateTime.now(),
                "Inventario, ventas y ofertas son datos ficticios; el saldo de caja y compromisos se consultan en /api/cash-flow/summary.");
    }
}
