package com.tdea.backend.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tdea.backend.domain.enums.CashMovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Movimiento de ingreso o gasto registrado para el flujo de caja demostrativo. */
@Entity
@Table(name = "cash_movements")
public class CashMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CashMovementType type;

    @Column(nullable = false, length = 160)
    private String description;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    /** Constructor requerido por JPA. */
    protected CashMovement() {
    }

    /**
     * Crea un movimiento de caja.
     *
     * @param movementDate fecha en que ocurrió el movimiento
     * @param type ingreso o gasto
     * @param description descripción breve
     * @param category categoría para agrupar el movimiento
     * @param amount monto positivo en COP
     */
    public CashMovement(LocalDate movementDate, CashMovementType type, String description,
            String category, BigDecimal amount) {
        this.movementDate = movementDate;
        this.type = type;
        this.description = description;
        this.category = category;
        this.amount = amount;
    }

    /** @return identificador persistido */
    public Long getId() { return id; }
    /** @return fecha del movimiento */
    public LocalDate getMovementDate() { return movementDate; }
    /** @return clase del movimiento */
    public CashMovementType getType() { return type; }
    /** @return descripción del movimiento */
    public String getDescription() { return description; }
    /** @return categoría del movimiento */
    public String getCategory() { return category; }
    /** @return monto positivo en COP */
    public BigDecimal getAmount() { return amount; }
}
