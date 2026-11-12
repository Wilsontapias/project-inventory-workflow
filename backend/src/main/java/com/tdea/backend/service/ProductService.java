package com.tdea.backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tdea.backend.api.dto.product.ProductRequest;
import com.tdea.backend.api.dto.product.ProductResponse;
import com.tdea.backend.api.mapper.ProductMapper;
import com.tdea.backend.common.exception.ConflictException;
import com.tdea.backend.common.exception.ResourceNotFoundException;
import com.tdea.backend.domain.entity.Product;
import com.tdea.backend.domain.entity.Supplier;
import com.tdea.backend.domain.enums.ProductCategory;
import com.tdea.backend.domain.enums.ProductStatus;
import com.tdea.backend.repository.ProductRepository;
import com.tdea.backend.repository.SupplierOfferRepository;
import com.tdea.backend.repository.SupplierRepository;

/** Casos de uso del catálogo de productos y sus filtros de inventario. */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierOfferRepository supplierOfferRepository;
    private final ProductMapper productMapper;

    /**
     * Construye el servicio con sus dependencias de persistencia y mapeo.
     *
     * @param productRepository persistencia de productos
     * @param supplierRepository persistencia de proveedores
     * @param supplierOfferRepository persistencia de ofertas
     * @param productMapper conversor entidad-DTO
     */
    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository,
            SupplierOfferRepository supplierOfferRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.supplierOfferRepository = supplierOfferRepository;
        this.productMapper = productMapper;
    }

    /**
     * Lista productos y aplica filtros opcionales sobre el conjunto de datos de demostración.
     *
     * @param search texto buscado en SKU, nombre o marca
     * @param category categoría del producto
     * @param status estado calculado del inventario
     * @param olderThanDays devuelve solo productos estrictamente más antiguos que el umbral
     * @param lowRotation filtra productos sin ventas durante 90 días y con antigüedad superior a 90 días
     * @return productos filtrados ordenados por nombre
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(String search, ProductCategory category,
            ProductStatus status, Integer olderThanDays, Boolean lowRotation) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .filter(product -> normalizedSearch.isBlank()
                        || product.getSku().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || product.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || product.getBrand().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .filter(product -> category == null || product.getCategory() == category)
                .filter(product -> status == null || product.calculateStatus() == status)
                .filter(product -> olderThanDays == null || product.getDaysInInventory() > olderThanDays)
                .filter(product -> lowRotation == null || product.isLowRotation(java.time.LocalDate.now()) == lowRotation)
                .map(productMapper::toResponse)
                .toList();
    }

    /**
     * Obtiene un producto por su identificador.
     *
     * @param id identificador del producto
     * @return producto encontrado
     */
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productMapper.toResponse(getProduct(id));
    }

    /**
     * Crea un producto luego de validar SKU, proveedor y umbrales de stock.
     *
     * @param request datos validados del producto
     * @return producto creado
     */
    public ProductResponse create(ProductRequest request) {
        validateStockThresholds(request);
        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new ConflictException("Ya existe un producto con el SKU " + request.sku() + ".");
        }
        Supplier supplier = getSupplier(request.preferredSupplierId());
        Product product = new Product(request.sku().trim(), request.name().trim(), request.brand().trim(),
                clean(request.model()), request.category(), clean(request.subcategory()), request.unitCost(),
                request.salePrice(), request.stockCurrent(), request.stockMinimum(), request.stockIdeal(),
                request.daysInInventory(), request.averageMonthlyDemand(), request.lastSaleDate(), supplier);
        return productMapper.toResponse(productRepository.save(product));
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id identificador del producto
     * @param request nuevos valores
     * @return producto actualizado
     */
    public ProductResponse update(Long id, ProductRequest request) {
        validateStockThresholds(request);
        Product product = getProduct(id);
        if (productRepository.existsBySkuIgnoreCaseAndIdNot(request.sku(), id)) {
            throw new ConflictException("Ya existe otro producto con el SKU " + request.sku() + ".");
        }
        Supplier supplier = getSupplier(request.preferredSupplierId());
        product.update(request.sku().trim(), request.name().trim(), request.brand().trim(),
                clean(request.model()), request.category(), clean(request.subcategory()), request.unitCost(),
                request.salePrice(), request.stockCurrent(), request.stockMinimum(), request.stockIdeal(),
                request.daysInInventory(), request.averageMonthlyDemand(), request.lastSaleDate(), supplier);
        return productMapper.toResponse(productRepository.save(product));
    }

    /**
     * Elimina un producto y las ofertas de demostración vinculadas.
     *
     * @param id identificador del producto
     */
    public void delete(Long id) {
        Product product = getProduct(id);
        supplierOfferRepository.deleteByProduct_Id(id);
        productRepository.delete(product);
    }

    /**
     * Obtiene la entidad de dominio o informa que no existe.
     *
     * @param id identificador del producto
     * @return entidad persistida
     */
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + id + "."));
    }

    private Supplier getSupplier(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el proveedor con id " + id + "."));
    }

    private void validateStockThresholds(ProductRequest request) {
        if (request.stockMinimum() > request.stockIdeal()) {
            throw new IllegalArgumentException("El stock mínimo no puede ser mayor que el stock ideal.");
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
