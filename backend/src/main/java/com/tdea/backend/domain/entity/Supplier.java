package com.tdea.backend.domain.entity;

import java.math.BigDecimal;

import com.tdea.backend.domain.enums.SupplierCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Proveedor de productos tecnológicos y sus condiciones comerciales básicas.
 *
 * <p>No almacena datos bancarios. El prototipo carece de autenticación y no debe
 * contener información financiera o personal real.</p>
 */
@Entity
@Table(name = "suppliers", uniqueConstraints = @UniqueConstraint(name = "uk_supplier_tax_id", columnNames = "tax_id"))
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tax_id", nullable = false, length = 30)
    private String taxId;

    @Column(name = "business_name", nullable = false, length = 160)
    private String businessName;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(length = 180)
    private String address;

    @Column(name = "billing_email", length = 140)
    private String billingEmail;

    @Column(length = 40)
    private String phone;

    @Column(name = "contact_name", length = 120)
    private String contactName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupplierCategory category;

    @Column(name = "payment_term_days", nullable = false)
    private Integer paymentTermDays;

    @Column(name = "lead_time_business_days", nullable = false)
    private Integer leadTimeBusinessDays;

    @Column(name = "credit_limit", nullable = false, precision = 16, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "credit_used", nullable = false, precision = 16, scale = 2)
    private BigDecimal creditUsed;

    @Column(name = "performance_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal performanceScore;

    @Column(name = "represented_brands", length = 300)
    private String representedBrands;

    /** Constructor requerido por JPA. */
    protected Supplier() {
    }

    /**
     * Crea un proveedor con sus datos comerciales.
     *
     * @param taxId identificador tributario ficticio o real de prueba
     * @param businessName razón social
     * @param city ciudad principal
     * @param address dirección comercial
     * @param billingEmail correo de facturación
     * @param phone teléfono de contacto
     * @param contactName contacto comercial
     * @param category categoría principal
     * @param paymentTermDays plazo acordado de pago
     * @param leadTimeBusinessDays tiempo de entrega en días hábiles
     * @param creditLimit cupo de crédito en COP
     * @param creditUsed cupo utilizado en COP
     * @param performanceScore calificación porcentual de entregas
     * @param representedBrands marcas representadas
     */
    public Supplier(String taxId, String businessName, String city, String address, String billingEmail,
            String phone, String contactName, SupplierCategory category, Integer paymentTermDays,
            Integer leadTimeBusinessDays, BigDecimal creditLimit, BigDecimal creditUsed,
            BigDecimal performanceScore, String representedBrands) {
        this.taxId = taxId;
        this.businessName = businessName;
        this.city = city;
        this.address = address;
        this.billingEmail = billingEmail;
        this.phone = phone;
        this.contactName = contactName;
        this.category = category;
        this.paymentTermDays = paymentTermDays;
        this.leadTimeBusinessDays = leadTimeBusinessDays;
        this.creditLimit = creditLimit;
        this.creditUsed = creditUsed;
        this.performanceScore = performanceScore;
        this.representedBrands = representedBrands;
    }

    /**
     * Actualiza los datos editables del proveedor.
     *
     * @param taxId identificador tributario
     * @param businessName razón social
     * @param city ciudad principal
     * @param address dirección
     * @param billingEmail correo de facturación
     * @param phone teléfono
     * @param contactName contacto comercial
     * @param category categoría comercial
     * @param paymentTermDays plazo de pago
     * @param leadTimeBusinessDays tiempo de entrega en días hábiles
     * @param creditLimit cupo máximo en COP
     * @param creditUsed cupo usado en COP
     * @param performanceScore calificación porcentual
     * @param representedBrands marcas representadas
     */
    public void update(String taxId, String businessName, String city, String address, String billingEmail,
            String phone, String contactName, SupplierCategory category, Integer paymentTermDays,
            Integer leadTimeBusinessDays, BigDecimal creditLimit, BigDecimal creditUsed,
            BigDecimal performanceScore, String representedBrands) {
        this.taxId = taxId;
        this.businessName = businessName;
        this.city = city;
        this.address = address;
        this.billingEmail = billingEmail;
        this.phone = phone;
        this.contactName = contactName;
        this.category = category;
        this.paymentTermDays = paymentTermDays;
        this.leadTimeBusinessDays = leadTimeBusinessDays;
        this.creditLimit = creditLimit;
        this.creditUsed = creditUsed;
        this.performanceScore = performanceScore;
        this.representedBrands = representedBrands;
    }

    /** @return identificador persistido */
    public Long getId() { return id; }
    /** @return identificador tributario */
    public String getTaxId() { return taxId; }
    /** @return razón social */
    public String getBusinessName() { return businessName; }
    /** @return ciudad principal */
    public String getCity() { return city; }
    /** @return dirección comercial */
    public String getAddress() { return address; }
    /** @return correo de facturación */
    public String getBillingEmail() { return billingEmail; }
    /** @return teléfono de contacto */
    public String getPhone() { return phone; }
    /** @return nombre del contacto comercial */
    public String getContactName() { return contactName; }
    /** @return categoría principal del proveedor */
    public SupplierCategory getCategory() { return category; }
    /** @return plazo de pago acordado en días */
    public Integer getPaymentTermDays() { return paymentTermDays; }
    /** @return tiempo de entrega en días hábiles */
    public Integer getLeadTimeBusinessDays() { return leadTimeBusinessDays; }
    /** @return cupo de crédito en COP */
    public BigDecimal getCreditLimit() { return creditLimit; }
    /** @return cupo utilizado en COP */
    public BigDecimal getCreditUsed() { return creditUsed; }
    /** @return evaluación porcentual de cumplimiento */
    public BigDecimal getPerformanceScore() { return performanceScore; }
    /** @return lista textual de marcas representadas */
    public String getRepresentedBrands() { return representedBrands; }
}
