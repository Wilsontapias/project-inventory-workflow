package com.tdea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tdea.backend.domain.entity.Product;

/** Acceso a persistencia y consultas de catálogo de productos. */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca si ya existe un SKU ignorando mayúsculas y minúsculas.
     * @param sku código que se desea verificar
     * @return {@code true} si ya existe
     */
    boolean existsBySkuIgnoreCase(String sku);

    /**
     * Busca si otro producto ya usa el SKU indicado.
     * @param sku código que se desea verificar
     * @param id identificador que debe excluirse
     * @return {@code true} si existe otro producto con el código
     */
    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    /**
     * Comprueba si un proveedor es el proveedor principal de algún producto.
     * @param supplierId identificador del proveedor
     * @return {@code true} si el proveedor está referenciado
     */
    boolean existsByPreferredSupplier_Id(Long supplierId);
}
