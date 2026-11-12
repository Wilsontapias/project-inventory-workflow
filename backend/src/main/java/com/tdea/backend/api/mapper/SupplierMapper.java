package com.tdea.backend.api.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.tdea.backend.api.dto.supplier.SupplierOfferResponse;
import com.tdea.backend.api.dto.supplier.SupplierResponse;
import com.tdea.backend.domain.entity.Supplier;
import com.tdea.backend.domain.entity.SupplierOffer;

/** Convierte proveedores y ofertas de dominio a DTOs REST. */
@Component
public class SupplierMapper {

    /**
     * Convierte un proveedor e incluye el cupo disponible calculado.
     *
     * @param supplier proveedor persistido
     * @return DTO comercial del proveedor
     */
    public SupplierResponse toResponse(Supplier supplier) {
        BigDecimal availableCredit = supplier.getCreditLimit().subtract(supplier.getCreditUsed()).max(BigDecimal.ZERO);
        return new SupplierResponse(
                supplier.getId(), supplier.getTaxId(), supplier.getBusinessName(), supplier.getCity(),
                supplier.getAddress(), supplier.getBillingEmail(), supplier.getPhone(), supplier.getContactName(),
                supplier.getCategory(), supplier.getPaymentTermDays(), supplier.getLeadTimeBusinessDays(),
                supplier.getCreditLimit(), supplier.getCreditUsed(), availableCredit,
                supplier.getPerformanceScore(), supplier.getRepresentedBrands());
    }

    /**
     * Convierte una oferta a DTO y calcula el costo total para la cantidad indicada.
     *
     * @param offer oferta persistida
     * @param quantity unidades que se desean reabastecer
     * @return DTO de oferta con total estimado
     */
    public SupplierOfferResponse toOfferResponse(SupplierOffer offer, int quantity) {
        return new SupplierOfferResponse(
                offer.getId(),
                offer.getSupplier().getId(),
                offer.getSupplier().getBusinessName(),
                offer.getUnitCost(),
                offer.getAvailableStock(),
                offer.getLeadTimeDays(),
                offer.getPaymentTermDays(),
                offer.getUnitCost().multiply(BigDecimal.valueOf(quantity)));
    }
}
