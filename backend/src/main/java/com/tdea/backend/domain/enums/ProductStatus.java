package com.tdea.backend.domain.enums;

/** Estados de inventario calculados a partir de las existencias y sus umbrales. */
public enum ProductStatus {
    /** Stock igual o inferior al mínimo configurado. */
    CRITICAL,
    /** Stock entre el mínimo y el nivel ideal, inclusive. */
    OPTIMAL,
    /** Stock superior al nivel ideal. */
    OVERSTOCK
}
