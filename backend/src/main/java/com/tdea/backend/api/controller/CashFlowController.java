package com.tdea.backend.api.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tdea.backend.api.dto.cashflow.CashFlowSummaryResponse;
import com.tdea.backend.api.dto.cashflow.CashMovementRequest;
import com.tdea.backend.api.dto.cashflow.CashMovementResponse;
import com.tdea.backend.api.dto.cashflow.PurchaseCommitmentRequest;
import com.tdea.backend.api.dto.cashflow.PurchaseCommitmentResponse;
import com.tdea.backend.domain.enums.CommitmentStatus;
import com.tdea.backend.service.CashFlowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/** Endpoints para resumen de caja, movimientos demostrativos y compromisos de pago. */
@Validated
@RestController
@RequestMapping("/api/cash-flow")
public class CashFlowController {

    private final CashFlowService cashFlowService;

    /**
     * Construye el controlador de flujo de caja.
     *
     * @param cashFlowService casos de uso financieros demostrativos
     */
    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    /**
     * Calcula los indicadores para un período y capacidad de reorden a 30 días.
     *
     * @param from inicio del período, opcional
     * @param to fin del período, opcional
     * @return resumen de caja ficticio
     */
    @GetMapping("/summary")
    public CashFlowSummaryResponse getSummary(@RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return cashFlowService.getSummary(from, to);
    }

    /**
     * Lista ingresos y gastos del período.
     *
     * @param from fecha inicial inclusiva
     * @param to fecha final inclusiva
     * @return movimientos ordenados cronológicamente
     */
    @GetMapping("/movements")
    public List<CashMovementResponse> findMovements(@RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return cashFlowService.findMovements(from, to);
    }

    /**
     * Consulta un movimiento por identificador.
     * @param id identificador del movimiento
     * @return movimiento encontrado
     */
    @GetMapping("/movements/{id}")
    public CashMovementResponse findMovementById(@PathVariable @Min(1) Long id) {
        return cashFlowService.findMovementById(id);
    }

    /**
     * Registra un movimiento de ingreso o gasto.
     *
     * @param request datos validados del movimiento
     * @return respuesta HTTP 201 con la URI del recurso
     */
    @PostMapping("/movements")
    public ResponseEntity<CashMovementResponse> createMovement(@Valid @RequestBody CashMovementRequest request) {
        CashMovementResponse response = cashFlowService.createMovement(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lista compromisos, opcionalmente filtrados por estado.
     *
     * @param status estado opcional
     * @return compromisos ordenados por vencimiento
     */
    @GetMapping("/commitments")
    public List<PurchaseCommitmentResponse> findCommitments(
            @RequestParam(required = false) CommitmentStatus status) {
        return cashFlowService.findCommitments(status);
    }

    /**
     * Consulta un compromiso por identificador.
     * @param id identificador del compromiso
     * @return compromiso encontrado
     */
    @GetMapping("/commitments/{id}")
    public PurchaseCommitmentResponse findCommitmentById(@PathVariable @Min(1) Long id) {
        return cashFlowService.findCommitmentById(id);
    }

    /**
     * Registra un compromiso pendiente.
     *
     * @param request datos validados
     * @return respuesta HTTP 201 y compromiso creado
     */
    @PostMapping("/commitments")
    public ResponseEntity<PurchaseCommitmentResponse> createCommitment(
            @Valid @RequestBody PurchaseCommitmentRequest request) {
        PurchaseCommitmentResponse response = cashFlowService.createCommitment(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Paga un compromiso y registra la salida correspondiente.
     *
     * @param id identificador del compromiso
     * @return compromiso liquidado
     */
    @PatchMapping("/commitments/{id}/pay")
    public PurchaseCommitmentResponse markCommitmentPaid(@PathVariable @Min(1) Long id) {
        return cashFlowService.markCommitmentPaid(id);
    }
}
