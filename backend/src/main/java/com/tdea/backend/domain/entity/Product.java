package com.tdea.backend.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.ProductCategory;
import com.tdea.backend.domain.enums.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Producto del catálogo con sus niveles de inventario, costos y proveedor principal.
 */
@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_product_sku", columnNames = "sku"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String sku;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductCategory category;

    @Column(length = 100)
    private String subcategory;

    @Column(name = "unit_cost", nullable = false, precision = 16, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "sale_price", nullable = false, precision = 16, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "stock_current", nullable = false)
    private Integer stockCurrent;

    @Column(name = "stock_minimum", nullable = false)
    private Integer stockMinimum;

    @Column(name = "stock_ideal", nullable = false)
    private Integer stockIdeal;

    @Column(name = "days_in_inventory", nullable = false)
    private Integer daysInInventory;

    @Column(name = "average_monthly_demand", nullable = false, precision = 10, scale = 2)
    private BigDecimal averageMonthlyDemand;

    @Column(name = "last_sale_date")
    private LocalDate lastSaleDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preferred_supplier_id", nullable = false)
    private Supplier preferredSupplier;

    /** Constructor requerido por JPA. */
    protected Product() {
    }

    /**
     * Crea un producto con información del catálogo e inventario.
     *
     * @param sku código único del producto
     * @param name nombre legible
     * @param brand marca
     * @param model modelo o referencia
     * @param category categoría principal
     * @param subcategory subcategoría
     * @param unitCost costo de adquisición unitario en COP
     * @param salePrice precio unitario de venta en COP
     * @param stockCurrent existencias actuales
     * @param stockMinimum umbral mínimo de reabastecimiento
     * @param stockIdeal nivel objetivo de inventario
     * @param daysInInventory días de permanencia en bodega
     * @param averageMonthlyDemand demanda media mensual en unidades
     * @param lastSaleDate fecha de la última venta registrada, si existe
     * @param preferredSupplier proveedor principal
     */
    public Product(String sku, String name, String brand, String model, ProductCategory category,
            String subcategory, BigDecimal unitCost, BigDecimal salePrice, Integer stockCurrent,
            Integer stockMinimum, Integer stockIdeal, Integer daysInInventory,
            BigDecimal averageMonthlyDemand, LocalDate lastSaleDate, Supplier preferredSupplier) {
        this.sku = sku;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.subcategory = subcategory;
        this.unitCost = unitCost;
        this.salePrice = salePrice;
        this.stockCurrent = stockCurrent;
        this.stockMinimum = stockMinimum;
        this.stockIdeal = stockIdeal;
        this.daysInInventory = daysInInventory;
        this.averageMonthlyDemand = averageMonthlyDemand;
        this.lastSaleDate = lastSaleDate;
        this.preferredSupplier = preferredSupplier;
    }

    /**
     * Actualiza la información editable del producto.
     *
     * @param sku código único del producto
     * @param name nombre legible
     * @param brand marca
     * @param model modelo o referencia
     * @param category categoría principal
     * @param subcategory subcategoría
     * @param unitCost costo de adquisición unitario en COP
     * @param salePrice precio unitario de venta en COP
     * @param stockCurrent existencias actuales
     * @param stockMinimum umbral mínimo
     * @param stockIdeal nivel objetivo
     * @param daysInInventory días en bodega
     * @param averageMonthlyDemand demanda mensual media
     * @param lastSaleDate fecha de la última venta registrada, si existe
     * @param preferredSupplier proveedor principal
     */
    public void update(String sku, String name, String brand, String model, ProductCategory category,
            String subcategory, BigDecimal unitCost, BigDecimal salePrice, Integer stockCurrent,
            Integer stockMinimum, Integer stockIdeal, Integer daysInInventory,
            BigDecimal averageMonthlyDemand, LocalDate lastSaleDate, Supplier preferredSupplier) {
        this.sku = sku;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.subcategory = subcategory;
        this.unitCost = unitCost;
        this.salePrice = salePrice;
        this.stockCurrent = stockCurrent;
        this.stockMinimum = stockMinimum;
        this.stockIdeal = stockIdeal;
        this.daysInInventory = daysInInventory;
        this.averageMonthlyDemand = averageMonthlyDemand;
        this.lastSaleDate = lastSaleDate;
        this.preferredSupplier = preferredSupplier;
    }

    /**
     * Calcula el estado del inventario según el mínimo y el ideal registrados.
     *
     * @return CRITICAL si actual &lt;= mínimo, OVERSTOCK si supera el ideal, u OPTIMAL
     */
    public ProductStatus calculateStatus() {
        if (stockCurrent <= stockMinimum) {
            return ProductStatus.CRITICAL;
        }
        if (stockCurrent > stockIdeal) {
            return ProductStatus.OVERSTOCK;
        }
        return ProductStatus.OPTIMAL;
    }

    /**
     * Calcula el valor de adquisición de las unidades actualmente almacenadas.
     *
     * @return costo unitario multiplicado por las existencias actuales
     */
    public BigDecimal calculateInventoryValue() {
        return unitCost.multiply(BigDecimal.valueOf(stockCurrent));
    }

    /**
     * Determina si el producto cumple la regla aprobada de baja rotación.
     *
     * @param referenceDate fecha de corte para el cálculo
     * @return verdadero si conserva stock, tiene más de 90 días en inventario y no registra ventas recientes
     */
    public boolean isLowRotation(LocalDate referenceDate) {
        LocalDate salesCutoff = referenceDate.minusDays(90);
        return stockCurrent > 0 && daysInInventory > 90
                && (lastSaleDate == null || lastSaleDate.isBefore(salesCutoff));
    }

    /** @return identificador persistido */
    public Long getId() { return id; }
    /** @return SKU único */
    public String getSku() { return sku; }
    /** @return nombre del producto */
    public String getName() { return name; }
    /** @return marca comercial */
    public String getBrand() { return brand; }
    /** @return modelo o referencia */
    public String getModel() { return model; }
    /** @return categoría del producto */
    public ProductCategory getCategory() { return category; }
    /** @return subcategoría del producto */
    public String getSubcategory() { return subcategory; }
    /** @return costo unitario de adquisición en COP */
    public BigDecimal getUnitCost() { return unitCost; }
    /** @return precio unitario de venta en COP */
    public BigDecimal getSalePrice() { return salePrice; }
    /** @return existencias actuales */
    public Integer getStockCurrent() { return stockCurrent; }
    /** @return umbral mínimo de inventario */
    public Integer getStockMinimum() { return stockMinimum; }
    /** @return nivel ideal de inventario */
    public Integer getStockIdeal() { return stockIdeal; }
    /** @return días de permanencia en bodega */
    public Integer getDaysInInventory() { return daysInInventory; }
    /** @return demanda media mensual en unidades */
    public BigDecimal getAverageMonthlyDemand() { return averageMonthlyDemand; }
    /** @return fecha de la última venta registrada, o null si no hay ventas históricas */
    public LocalDate getLastSaleDate() { return lastSaleDate; }
    /** @return proveedor principal */
    public Supplier getPreferredSupplier() { return preferredSupplier; }
}
