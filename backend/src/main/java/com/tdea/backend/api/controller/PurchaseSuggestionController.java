package com.tdea.backend.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tdea.backend.api.dto.purchase.PurchaseSuggestionResponse;
import com.tdea.backend.api.dto.supplier.SupplierOfferResponse;
import com.tdea.backend.service.PurchaseSuggestionService;

/** Endpoints REST para sugerencias de compra y comparación de ofertas. */
@RestController
@RequestMapping("/api")
public class PurchaseSuggestionController {

    private final PurchaseSuggestionService purchaseSuggestionService;

    /**
     * Inyecta el servicio de recomendaciones.
     *
     * @param purchaseSuggestionService caso de uso de recomendaciones
     */
    public PurchaseSuggestionController(PurchaseSuggestionService purchaseSuggestionService) {
        this.purchaseSuggestionService = purchaseSuggestionService;
    }

    /**
     * Obtiene recomendaciones calculadas para productos bajo el stock mínimo.
     *
     * @param search filtro parcial opcional por SKU o nombre
     * @return recomendaciones con justificación y alternativas comerciales
     */
    @GetMapping("/purchase-suggestions")
    public List<PurchaseSuggestionResponse> findSuggestions(@RequestParam(required = false) String search) {
        return purchaseSuggestionService.findAll(search);
    }

    /**
     * Compara las ofertas disponibles para un producto.
     *
     * @param productId identificador del producto
     * @return cotizaciones de proveedor ordenadas por costo
     */
    @GetMapping("/products/{productId}/supplier-offers")
    public List<SupplierOfferResponse> findOffers(@PathVariable Long productId) {
        return purchaseSuggestionService.findOffers(productId);
    }
}
