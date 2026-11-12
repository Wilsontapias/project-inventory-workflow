package com.tdea.backend.domain.enums;

/** Estado de un compromiso de pago futuro. */
public enum CommitmentStatus {
    /** Obligación pendiente de pago. */
    OPEN,
    /** Obligación pagada y reflejada como gasto de caja. */
    PAID
}
