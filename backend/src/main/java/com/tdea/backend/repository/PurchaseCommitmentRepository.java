package com.tdea.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tdea.backend.domain.entity.PurchaseCommitment;
import com.tdea.backend.domain.enums.CommitmentStatus;

/** Persistencia de compromisos de pago y consultas de vencimientos. */
public interface PurchaseCommitmentRepository extends JpaRepository<PurchaseCommitment, Long> {

    /**
     * Busca compromisos de un estado con vencimiento dentro del rango inclusivo.
     *
     * @param status estado del compromiso
     * @param start fecha inicial
     * @param end fecha final
     * @return compromisos ordenados por vencimiento
     */
    List<PurchaseCommitment> findAllByStatusAndDueDateBetweenOrderByDueDateAsc(
            CommitmentStatus status, LocalDate start, LocalDate end);

    /**
     * Busca todos los compromisos de un estado ordenados por vencimiento.
     *
     * @param status estado solicitado
     * @return lista de compromisos
     */
    List<PurchaseCommitment> findAllByStatusOrderByDueDateAsc(CommitmentStatus status);

    /**
     * Comprueba si el proveedor aparece en algún compromiso de pago.
     *
     * @param supplierId identificador del proveedor
     * @return {@code true} si hay compromisos asociados
     */
    boolean existsBySupplier_Id(Long supplierId);
}
