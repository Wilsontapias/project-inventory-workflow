package com.tdea.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tdea.backend.domain.entity.Supplier;

/** Acceso a persistencia y consultas del directorio de proveedores. */
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Busca un proveedor por identificador tributario sin distinguir mayúsculas.
     * @param taxId identificador tributario
     * @return {@code true} si existe
     */
    boolean existsByTaxIdIgnoreCase(String taxId);

    /**
     * Busca si otro proveedor ya usa el identificador tributario indicado.
     * @param taxId identificador tributario
     * @param id identificador que debe excluirse
     * @return {@code true} si existe otro proveedor con el valor
     */
    boolean existsByTaxIdIgnoreCaseAndIdNot(String taxId, Long id);

    /**
     * Filtra por nombre o identificador tributario.
     * @param businessName texto de búsqueda para razón social
     * @param taxId texto de búsqueda para identificación
     * @return proveedores coincidentes
     */
    List<Supplier> findByBusinessNameContainingIgnoreCaseOrTaxIdContainingIgnoreCase(
            String businessName, String taxId);
}
