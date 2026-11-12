package com.tdea.backend.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.CommitmentStatus;
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

/** Obligación de pago futura que reduce la capacidad disponible de reorden. */
@Entity
@Table(name = "purchase_commitments")
public class PurchaseCommitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, length = 160)
    private String description;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CommitmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    /** Constructor requerido por JPA. */
    protected PurchaseCommitment() {
    }

    /**
     * Crea un compromiso de pago pendiente.
     *
     * @param dueDate fecha esperada de vencimiento
     * @param description detalle del compromiso
     * @param amount monto positivo en COP
     * @param supplier proveedor asociado, si aplica
     */
    public PurchaseCommitment(LocalDate dueDate, String description, BigDecimal amount, Supplier supplier) {
        this.dueDate = dueDate;
        this.description = description;
        this.amount = amount;
        this.supplier = supplier;
        this.status = CommitmentStatus.OPEN;
    }

    /** Marca como pagado un compromiso que estaba pendiente. */
    public void markPaid() {
        this.status = CommitmentStatus.PAID;
    }

    /** @return identificador persistido */
    public Long getId() { return id; }
    /** @return fecha de vencimiento */
    public LocalDate getDueDate() { return dueDate; }
    /** @return descripción del compromiso */
    public String getDescription() { return description; }
    /** @return monto en COP */
    public BigDecimal getAmount() { return amount; }
    /** @return estado del compromiso */
    public CommitmentStatus getStatus() { return status; }
    /** @return proveedor asociado o null */
    public Supplier getSupplier() { return supplier; }
}
