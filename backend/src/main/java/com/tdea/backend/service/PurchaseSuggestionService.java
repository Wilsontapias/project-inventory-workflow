package com.tdea.backend.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tdea.backend.api.dto.purchase.PurchaseSuggestionResponse;
import com.tdea.backend.api.dto.supplier.SupplierOfferResponse;
import com.tdea.backend.api.mapper.SupplierMapper;
import com.tdea.backend.domain.entity.Product;
import com.tdea.backend.domain.entity.SupplierOffer;
import com.tdea.backend.repository.ProductRepository;
import com.tdea.backend.repository.SupplierOfferRepository;

/** Calcula sugerencias de reabastecimiento y compara las ofertas disponibles. */
@Service
@Transactional(readOnly = true)
public class PurchaseSuggestionService {

    private final ProductRepository productRepository;
    private final SupplierOfferRepository supplierOfferRepository;
    private final SupplierMapper supplierMapper;

    /**
     * Construye el servicio de recomendaciones.
     *
     * @param productRepository persistencia de productos
     * @param supplierOfferRepository persistencia de ofertas
     * @param supplierMapper conversor de ofertas a DTOs
     */
    public PurchaseSuggestionService(ProductRepository productRepository,
            SupplierOfferRepository supplierOfferRepository, SupplierMapper supplierMapper) {
        this.productRepository = productRepository;
        this.supplierOfferRepository = supplierOfferRepository;
        this.supplierMapper = supplierMapper;
    }

    /**
     * Calcula sugerencias para productos bajo el umbral mínimo de inventario.
     *
     * @param search filtro opcional por SKU o nombre
     * @return recomendaciones con alternativas de proveedor y costo estimado
     */
    public List<PurchaseSuggestionResponse> findAll(String search) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return productRepository.findAll().stream()
                .filter(product -> product.getStockCurrent() <= product.getStockMinimum())
                .filter(product -> product.getStockCurrent() < product.getStockIdeal())
                .filter(product -> normalizedSearch.isBlank()
                        || product.getSku().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || product.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .map(this::toSuggestion)
                .sorted(Comparator.comparing(PurchaseSuggestionResponse::estimatedTotal).reversed())
                .toList();
    }

    /**
     * Obtiene las cotizaciones de proveedor para un producto.
     *
     * @param productId identificador del producto
     * @return ofertas ordenadas por costo unitario
     */
    public List<SupplierOfferResponse> findOffers(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new com.tdea.backend.common.exception.ResourceNotFoundException(
                        "No existe el producto con id " + productId + "."));
        int quantity = Math.max(0, product.getStockIdeal() - product.getStockCurrent());
        return supplierOfferRepository.findByProduct_IdOrderByUnitCostAsc(productId).stream()
                .map(offer -> supplierMapper.toOfferResponse(offer, quantity))
                .toList();
    }

    /**
     * Suma el valor estimado de todas las recomendaciones actuales.
     *
     * @return valor agregado de reabastecimiento estimado en COP
     */
    public BigDecimal totalSuggestedReorderValue() {
        return findAll(null).stream()
                .map(PurchaseSuggestionResponse::estimatedTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PurchaseSuggestionResponse toSuggestion(Product product) {
        int quantity = Math.max(0, product.getStockIdeal() - product.getStockCurrent());
        List<SupplierOffer> offers = supplierOfferRepository.findByProduct_IdOrderByUnitCostAsc(product.getId());

        SupplierOffer selectedOffer = offers.stream()
                .filter(offer -> offer.getAvailableStock() >= quantity)
                .min(Comparator.comparing(SupplierOffer::getUnitCost)
                        .thenComparing(SupplierOffer::getLeadTimeDays))
                .orElseGet(() -> offers.stream().findFirst().orElse(null));

        List<SupplierOfferResponse> alternatives = offers.stream()
                .map(offer -> supplierMapper.toOfferResponse(offer, quantity))
                .toList();

        BigDecimal unitCost = selectedOffer == null ? product.getUnitCost() : selectedOffer.getUnitCost();
        BigDecimal total = unitCost.multiply(BigDecimal.valueOf(quantity));
        String supplierName = selectedOffer == null
                ? product.getPreferredSupplier().getBusinessName()
                : selectedOffer.getSupplier().getBusinessName();
        Long supplierId = selectedOffer == null
                ? product.getPreferredSupplier().getId()
                : selectedOffer.getSupplier().getId();

        String availabilityNote = selectedOffer != null && selectedOffer.getAvailableStock() < quantity
                ? " Ninguna oferta cubre toda la cantidad sugerida; validar disponibilidad antes de comprar."
                : "";
        String justification = "Stock actual " + product.getStockCurrent() + " <= mínimo "
                + product.getStockMinimum() + "; se sugieren " + quantity + " unidades para llegar al ideal "
                + product.getStockIdeal() + ". Demanda media: " + product.getAverageMonthlyDemand()
                + " unidades/mes." + availabilityNote;

        return new PurchaseSuggestionResponse(product.getId(), product.getSku(), product.getName(),
                product.getStockCurrent(), product.getStockMinimum(), product.getStockIdeal(), quantity,
                total, supplierId, supplierName, justification, alternatives);
    }
}
