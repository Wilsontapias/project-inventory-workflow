package com.tdea.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tdea.backend.domain.entity.SupplierOffer;

/** Consultas de las ofertas de proveedor asociadas a productos. */
public interface SupplierOfferRepository extends JpaRepository<SupplierOffer, Long> {

    /**
     * Devuelve las ofertas de un producto ordenadas por precio ascendente.
     * @param productId identificador del producto
     * @return ofertas del producto ordenadas por costo
     */
    List<SupplierOffer> findByProduct_IdOrderByUnitCostAsc(Long productId);

    /**
     * Devuelve las ofertas de un producto.
     * @param productId identificador del producto
     * @return ofertas asociadas
     */
    List<SupplierOffer> findByProduct_Id(Long productId);

    /**
     * Comprueba si existen ofertas asociadas a un proveedor.
     * @param supplierId identificador del proveedor
     * @return {@code true} si existe al menos una oferta
     */
    boolean existsBySupplier_Id(Long supplierId);

    /**
     * Elimina las ofertas de un producto antes de eliminarlo en el prototipo.
     * @param productId identificador del producto
     */
    void deleteByProduct_Id(Long productId);
}
