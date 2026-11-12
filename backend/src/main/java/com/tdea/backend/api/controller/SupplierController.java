package com.tdea.backend.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tdea.backend.api.dto.supplier.SupplierRequest;
import com.tdea.backend.api.dto.supplier.SupplierResponse;
import com.tdea.backend.domain.enums.SupplierCategory;
import com.tdea.backend.service.SupplierService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/** Endpoints REST para consultar y administrar el directorio de proveedores. */
@Validated
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Inyecta los casos de uso de proveedores.
     *
     * @param supplierService servicio del directorio de proveedores
     */
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Lista proveedores aplicando filtros opcionales.
     *
     * @param search razón social o identificación tributaria
     * @param category categoría comercial
     * @param paymentTermDays plazo de pago en días
     * @return proveedores encontrados
     */
    @GetMapping
    public List<SupplierResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SupplierCategory category,
            @RequestParam(required = false) @Min(0) Integer paymentTermDays) {
        return supplierService.findAll(search, category, paymentTermDays);
    }

    /**
     * Consulta un proveedor por identificador.
     *
     * @param id identificador del proveedor
     * @return datos del proveedor encontrado
     */
    @GetMapping("/{id}")
    public SupplierResponse findById(@PathVariable @Min(1) Long id) {
        return supplierService.findById(id);
    }

    /**
     * Registra un proveedor y devuelve HTTP 201.
     *
     * @param request datos validados
     * @return respuesta con ubicación y proveedor creado
     */
    @PostMapping
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Actualiza un proveedor existente.
     *
     * @param id identificador del proveedor
     * @param request datos nuevos validados
     * @return proveedor actualizado
     */
    @PutMapping("/{id}")
    public SupplierResponse update(@PathVariable @Min(1) Long id, @Valid @RequestBody SupplierRequest request) {
        return supplierService.update(id, request);
    }

    /**
     * Elimina un proveedor no referenciado por productos u ofertas.
     *
     * @param id identificador del proveedor
     * @return respuesta HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
