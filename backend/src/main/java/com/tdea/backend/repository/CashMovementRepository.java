package com.tdea.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tdea.backend.domain.entity.CashMovement;

/** Persistencia y filtros temporales de movimientos de caja. */
public interface CashMovementRepository extends JpaRepository<CashMovement, Long> {

    /**
     * Busca movimientos hasta una fecha para calcular saldo acumulado.
     *
     * @param date fecha máxima inclusiva
     * @return movimientos ordenados cronológicamente
     */
    List<CashMovement> findAllByMovementDateLessThanEqualOrderByMovementDateAsc(LocalDate date);

    /**
     * Busca movimientos dentro de un período, con límites inclusivos.
     *
     * @param start fecha inicial inclusiva
     * @param end fecha final inclusiva
     * @return movimientos del período
     */
    List<CashMovement> findAllByMovementDateBetweenOrderByMovementDateAsc(LocalDate start, LocalDate end);
}
