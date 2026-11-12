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

import com.tdea.backend.api.dto.product.ProductRequest;
import com.tdea.backend.api.dto.product.ProductResponse;
import com.tdea.backend.domain.enums.ProductCategory;
import com.tdea.backend.domain.enums.ProductStatus;
import com.tdea.backend.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/** Endpoints REST para consulta y mantenimiento del catálogo de productos. */
@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Inyecta los casos de uso del catálogo.
     *
     * @param productService servicio de productos
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Consulta el catálogo aplicando filtros opcionales.
     *
     * @param search búsqueda parcial por SKU, nombre o marca
     * @param category categoría del producto
     * @param status estado calculado del stock
     * @param olderThanDays umbral de antigüedad; el filtro usa estrictamente mayor que
     * @param lowRotation true para filtrar productos sin ventas recientes con edad >90 días
     * @return productos encontrados
     */
    @GetMapping
    public List<ProductResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) @Min(0) Integer olderThanDays,
            @RequestParam(required = false) Boolean lowRotation) {
        return productService.findAll(search, category, status, olderThanDays, lowRotation);
    }

    /**
     * Consulta un producto por identificador.
     *
     * @param id identificador del producto
     * @return producto encontrado
     */
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable @Min(1) Long id) {
        return productService.findById(id);
    }

    /**
     * Registra un producto y devuelve la ruta del recurso creado.
     *
     * @param request datos validados del producto
     * @return respuesta HTTP 201 con el producto y su URI
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id identificador del producto
     * @param request datos nuevos
     * @return producto actualizado
     */
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable @Min(1) Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    /**
     * Elimina un producto.
     *
     * @param id identificador del producto
     * @return respuesta HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
