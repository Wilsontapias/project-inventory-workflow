package com.tdea.backend.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Oferta de un proveedor para un producto, usada para comparar alternativas de compra.
 */
@Entity
@Table(name = "supplier_offers", uniqueConstraints = @UniqueConstraint(name = "uk_supplier_product_offer",
        columnNames = { "supplier_id", "product_id" }))
public class SupplierOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "unit_cost", nullable = false, precision = 16, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;

    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays;

    @Column(name = "payment_term_days", nullable = false)
    private Integer paymentTermDays;

    /** Constructor requerido por JPA. */
    protected SupplierOffer() {
    }

    /**
     * Crea una oferta comercial de un proveedor para un producto.
     *
     * @param supplier proveedor de la oferta
     * @param product producto cotizado
     * @param unitCost costo unitario en COP
     * @param availableStock unidades que el proveedor informa disponibles
     * @param leadTimeDays días estimados para entrega
     * @param paymentTermDays días de plazo de pago
     */
    public SupplierOffer(Supplier supplier, Product product, BigDecimal unitCost, Integer availableStock,
            Integer leadTimeDays, Integer paymentTermDays) {
        this.supplier = supplier;
        this.product = product;
        this.unitCost = unitCost;
        this.availableStock = availableStock;
        this.leadTimeDays = leadTimeDays;
        this.paymentTermDays = paymentTermDays;
    }

    /** @return identificador persistido */
    public Long getId() { return id; }
    /** @return proveedor de la oferta */
    public Supplier getSupplier() { return supplier; }
    /** @return producto cotizado */
    public Product getProduct() { return product; }
    /** @return precio unitario en COP */
    public BigDecimal getUnitCost() { return unitCost; }
    /** @return unidades informadas como disponibles */
    public Integer getAvailableStock() { return availableStock; }
    /** @return tiempo de entrega en días */
    public Integer getLeadTimeDays() { return leadTimeDays; }
    /** @return plazo de pago ofrecido en días */
    public Integer getPaymentTermDays() { return paymentTermDays; }
}
