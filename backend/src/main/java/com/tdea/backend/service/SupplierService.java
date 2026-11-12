package com.tdea.backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tdea.backend.api.dto.supplier.SupplierRequest;
import com.tdea.backend.api.dto.supplier.SupplierResponse;
import com.tdea.backend.api.mapper.SupplierMapper;
import com.tdea.backend.common.exception.ConflictException;
import com.tdea.backend.common.exception.ResourceNotFoundException;
import com.tdea.backend.domain.entity.Supplier;
import com.tdea.backend.domain.enums.SupplierCategory;
import com.tdea.backend.repository.ProductRepository;
import com.tdea.backend.repository.PurchaseCommitmentRepository;
import com.tdea.backend.repository.SupplierOfferRepository;
import com.tdea.backend.repository.SupplierRepository;

/** Casos de uso del directorio y mantenimiento de proveedores. */
@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final SupplierOfferRepository supplierOfferRepository;
    private final PurchaseCommitmentRepository commitmentRepository;
    private final SupplierMapper supplierMapper;

    /**
     * Construye el servicio de proveedores.
     *
     * @param supplierRepository persistencia de proveedores
     * @param productRepository persistencia de productos
     * @param supplierOfferRepository persistencia de ofertas
     * @param commitmentRepository persistencia de compromisos
     * @param supplierMapper conversor entidad-DTO
     */
    public SupplierService(SupplierRepository supplierRepository, ProductRepository productRepository,
            SupplierOfferRepository supplierOfferRepository, PurchaseCommitmentRepository commitmentRepository,
            SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.supplierOfferRepository = supplierOfferRepository;
        this.commitmentRepository = commitmentRepository;
        this.supplierMapper = supplierMapper;
    }

    /**
     * Lista proveedores con filtros de búsqueda, categoría y plazo comercial.
     *
     * @param search razón social o identificador tributario
     * @param category categoría comercial
     * @param paymentTermDays días de crédito acordados
     * @return proveedores filtrados alfabéticamente
     */
    @Transactional(readOnly = true)
    public List<SupplierResponse> findAll(String search, SupplierCategory category, Integer paymentTermDays) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return supplierRepository.findAll().stream()
                .filter(supplier -> normalizedSearch.isBlank()
                        || supplier.getBusinessName().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || supplier.getTaxId().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .filter(supplier -> category == null || supplier.getCategory() == category)
                .filter(supplier -> paymentTermDays == null || supplier.getPaymentTermDays().equals(paymentTermDays))
                .sorted((left, right) -> left.getBusinessName().compareToIgnoreCase(right.getBusinessName()))
                .map(supplierMapper::toResponse)
                .toList();
    }

    /**
     * Obtiene un proveedor por identificador.
     *
     * @param id identificador del proveedor
     * @return proveedor encontrado
     */
    @Transactional(readOnly = true)
    public SupplierResponse findById(Long id) {
        return supplierMapper.toResponse(getSupplier(id));
    }

    /**
     * Registra un proveedor y valida la unicidad de su identificador tributario.
     *
     * @param request datos validados
     * @return proveedor creado
     */
    public SupplierResponse create(SupplierRequest request) {
        validateCredit(request);
        if (supplierRepository.existsByTaxIdIgnoreCase(request.taxId())) {
            throw new ConflictException("Ya existe un proveedor con el identificador " + request.taxId() + ".");
        }
        Supplier supplier = new Supplier(request.taxId().trim(), request.businessName().trim(), request.city().trim(),
                clean(request.address()), clean(request.billingEmail()), clean(request.phone()),
                clean(request.contactName()), request.category(), request.paymentTermDays(),
                request.leadTimeBusinessDays(), request.creditLimit(), request.creditUsed(),
                request.performanceScore(), clean(request.representedBrands()));
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    /**
     * Actualiza un proveedor existente.
     *
     * @param id identificador del proveedor
     * @param request nuevos valores
     * @return proveedor actualizado
     */
    public SupplierResponse update(Long id, SupplierRequest request) {
        validateCredit(request);
        Supplier supplier = getSupplier(id);
        if (supplierRepository.existsByTaxIdIgnoreCaseAndIdNot(request.taxId(), id)) {
            throw new ConflictException("Ya existe otro proveedor con el identificador " + request.taxId() + ".");
        }
        supplier.update(request.taxId().trim(), request.businessName().trim(), request.city().trim(),
                clean(request.address()), clean(request.billingEmail()), clean(request.phone()),
                clean(request.contactName()), request.category(), request.paymentTermDays(),
                request.leadTimeBusinessDays(), request.creditLimit(), request.creditUsed(),
                request.performanceScore(), clean(request.representedBrands()));
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    /**
     * Elimina un proveedor solo si no está referenciado por productos u ofertas.
     *
     * @param id identificador del proveedor
     */
    public void delete(Long id) {
        Supplier supplier = getSupplier(id);
        if (productRepository.existsByPreferredSupplier_Id(id) || supplierOfferRepository.existsBySupplier_Id(id)
                || commitmentRepository.existsBySupplier_Id(id)) {
            throw new ConflictException("No se puede eliminar un proveedor asociado a productos, ofertas o compromisos.");
        }
        supplierRepository.delete(supplier);
    }

    /**
     * Obtiene la entidad persistida o informa que no existe.
     *
     * @param id identificador del proveedor
     * @return entidad persistida
     */
    @Transactional(readOnly = true)
    public Supplier getSupplier(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el proveedor con id " + id + "."));
    }

    private void validateCredit(SupplierRequest request) {
        if (request.creditUsed().compareTo(request.creditLimit()) > 0) {
            throw new IllegalArgumentException("El cupo utilizado no puede superar el límite de crédito.");
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
