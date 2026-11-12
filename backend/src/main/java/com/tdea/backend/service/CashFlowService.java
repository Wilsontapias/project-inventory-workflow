package com.tdea.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tdea.backend.api.dto.cashflow.CashFlowSummaryResponse;
import com.tdea.backend.api.dto.cashflow.CashMovementRequest;
import com.tdea.backend.api.dto.cashflow.CashMovementResponse;
import com.tdea.backend.api.dto.cashflow.PurchaseCommitmentRequest;
import com.tdea.backend.api.dto.cashflow.PurchaseCommitmentResponse;
import com.tdea.backend.common.exception.ConflictException;
import com.tdea.backend.common.exception.ResourceNotFoundException;
import com.tdea.backend.domain.entity.CashMovement;
import com.tdea.backend.domain.entity.PurchaseCommitment;
import com.tdea.backend.domain.entity.Supplier;
import com.tdea.backend.domain.enums.CashMovementType;
import com.tdea.backend.domain.enums.CommitmentStatus;
import com.tdea.backend.repository.CashMovementRepository;
import com.tdea.backend.repository.PurchaseCommitmentRepository;
import com.tdea.backend.repository.SupplierRepository;

/** Calcula flujo de caja ficticio y mantiene movimientos y compromisos de demostración. */
@Service
@Transactional
public class CashFlowService {

    private final CashMovementRepository cashMovementRepository;
    private final PurchaseCommitmentRepository commitmentRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseSuggestionService purchaseSuggestionService;
    private final BigDecimal openingBalance;

    /**
     * Construye el servicio con las dependencias y saldo inicial configurable.
     *
     * @param cashMovementRepository persistencia de ingresos y gastos
     * @param commitmentRepository persistencia de compromisos de pago
     * @param supplierRepository persistencia de proveedores
     * @param purchaseSuggestionService valor de sugerencias vigentes
     * @param openingBalance saldo inicial ficticio en COP
     */
    public CashFlowService(CashMovementRepository cashMovementRepository,
            PurchaseCommitmentRepository commitmentRepository, SupplierRepository supplierRepository,
            PurchaseSuggestionService purchaseSuggestionService,
            @Value("${app.cash.opening-balance-cop:320000000}") BigDecimal openingBalance) {
        this.cashMovementRepository = cashMovementRepository;
        this.commitmentRepository = commitmentRepository;
        this.supplierRepository = supplierRepository;
        this.purchaseSuggestionService = purchaseSuggestionService;
        this.openingBalance = openingBalance;
    }

    /**
     * Calcula el flujo del período y la capacidad neta para reorden.
     *
     * @param from inicio inclusivo; por defecto, primer día del mes actual
     * @param to fin inclusivo; por defecto, fecha actual
     * @return métricas de caja basadas en saldo y registros ficticios
     */
    @Transactional(readOnly = true)
    public CashFlowSummaryResponse getSummary(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate periodStart = from == null ? today.withDayOfMonth(1) : from;
        LocalDate periodEnd = to == null ? today : to;
        validateDateRange(periodStart, periodEnd);

        List<CashMovement> periodMovements = cashMovementRepository
                .findAllByMovementDateBetweenOrderByMovementDateAsc(periodStart, periodEnd);
        BigDecimal periodIncome = totalOfType(periodMovements, CashMovementType.INCOME);
        BigDecimal periodExpenses = totalOfType(periodMovements, CashMovementType.EXPENSE);

        BigDecimal cashBalance = cashMovementRepository
                .findAllByMovementDateLessThanEqualOrderByMovementDateAsc(today).stream()
                .map(movement -> movement.getType() == CashMovementType.INCOME
                        ? movement.getAmount() : movement.getAmount().negate())
                .reduce(openingBalance, BigDecimal::add);

        BigDecimal pendingCommitments = commitmentRepository
                .findAllByStatusAndDueDateBetweenOrderByDueDateAsc(
                        CommitmentStatus.OPEN, today, today.plusDays(30))
                .stream()
                .map(PurchaseCommitment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal availableForReorder = cashBalance.subtract(pendingCommitments);
        BigDecimal suggestedReorder = purchaseSuggestionService.totalSuggestedReorderValue();

        return new CashFlowSummaryResponse(
                periodStart,
                periodEnd,
                openingBalance,
                cashBalance,
                periodIncome,
                periodExpenses,
                periodIncome.subtract(periodExpenses),
                pendingCommitments,
                availableForReorder,
                suggestedReorder,
                availableForReorder.compareTo(suggestedReorder) >= 0,
                OffsetDateTime.now(),
                "Saldo, movimientos y compromisos son datos ficticios de demostración; no representan la caja real del negocio.");
    }

    /**
     * Lista movimientos dentro de un período.
     *
     * @param from fecha inicial inclusiva
     * @param to fecha final inclusiva
     * @return movimientos ordenados cronológicamente
     */
    @Transactional(readOnly = true)
    public List<CashMovementResponse> findMovements(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate periodStart = from == null ? today.withDayOfMonth(1) : from;
        LocalDate periodEnd = to == null ? today : to;
        validateDateRange(periodStart, periodEnd);
        return cashMovementRepository.findAllByMovementDateBetweenOrderByMovementDateAsc(periodStart, periodEnd)
                .stream().map(this::toMovementResponse).toList();
    }

    /**
     * Registra un ingreso o gasto efectivo.
     *
     * @param request datos validados del movimiento
     * @return movimiento creado
     */
    public CashMovementResponse createMovement(CashMovementRequest request) {
        CashMovement movement = new CashMovement(request.movementDate(), request.type(),
                request.description().trim(), request.category().trim(), request.amount());
        return toMovementResponse(cashMovementRepository.save(movement));
    }

    /**
     * Consulta un movimiento registrado.
     *
     * @param movementId identificador del movimiento
     * @return movimiento encontrado
     */
    @Transactional(readOnly = true)
    public CashMovementResponse findMovementById(Long movementId) {
        CashMovement movement = cashMovementRepository.findById(movementId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el movimiento con id " + movementId + "."));
        return toMovementResponse(movement);
    }

    /**
     * Lista compromisos pendientes o todos los compromisos.
     *
     * @param status filtro opcional de estado
     * @return compromisos ordenados por fecha de vencimiento
     */
    @Transactional(readOnly = true)
    public List<PurchaseCommitmentResponse> findCommitments(CommitmentStatus status) {
        List<PurchaseCommitment> commitments = status == null
                ? commitmentRepository.findAll().stream()
                        .sorted((left, right) -> left.getDueDate().compareTo(right.getDueDate())).toList()
                : commitmentRepository.findAllByStatusOrderByDueDateAsc(status);
        return commitments.stream().map(this::toCommitmentResponse).toList();
    }

    /**
     * Consulta un compromiso por identificador.
     *
     * @param commitmentId identificador del compromiso
     * @return compromiso encontrado
     */
    @Transactional(readOnly = true)
    public PurchaseCommitmentResponse findCommitmentById(Long commitmentId) {
        PurchaseCommitment commitment = commitmentRepository.findById(commitmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el compromiso con id " + commitmentId + "."));
        return toCommitmentResponse(commitment);
    }

    /**
     * Registra un compromiso de pago pendiente.
     *
     * @param request datos del compromiso
     * @return compromiso creado
     */
    public PurchaseCommitmentResponse createCommitment(PurchaseCommitmentRequest request) {
        Supplier supplier = request.supplierId() == null ? null : supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el proveedor con id " + request.supplierId() + "."));
        PurchaseCommitment commitment = new PurchaseCommitment(request.dueDate(), request.description().trim(),
                request.amount(), supplier);
        return toCommitmentResponse(commitmentRepository.save(commitment));
    }

    /**
     * Liquida un compromiso y registra su pago como gasto, evitando doble conteo.
     *
     * @param commitmentId identificador del compromiso
     * @return compromiso con estado PAID
     */
    public PurchaseCommitmentResponse markCommitmentPaid(Long commitmentId) {
        PurchaseCommitment commitment = commitmentRepository.findById(commitmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el compromiso con id " + commitmentId + "."));
        if (commitment.getStatus() == CommitmentStatus.PAID) {
            throw new ConflictException("El compromiso ya se encuentra pagado.");
        }

        commitment.markPaid();
        commitmentRepository.save(commitment);
        CashMovement payment = new CashMovement(LocalDate.now(), CashMovementType.EXPENSE,
                "Pago de compromiso: " + commitment.getDescription(), "Compromisos", commitment.getAmount());
        cashMovementRepository.save(payment);
        return toCommitmentResponse(commitment);
    }

    private BigDecimal totalOfType(List<CashMovement> movements, CashMovementType type) {
        return movements.stream()
                .filter(movement -> movement.getType() == type)
                .map(CashMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CashMovementResponse toMovementResponse(CashMovement movement) {
        return new CashMovementResponse(movement.getId(), movement.getMovementDate(), movement.getType(),
                movement.getDescription(), movement.getCategory(), movement.getAmount());
    }

    private PurchaseCommitmentResponse toCommitmentResponse(PurchaseCommitment commitment) {
        Supplier supplier = commitment.getSupplier();
        return new PurchaseCommitmentResponse(commitment.getId(), commitment.getDueDate(),
                commitment.getDescription(), commitment.getAmount(), commitment.getStatus(),
                supplier == null ? null : supplier.getId(), supplier == null ? null : supplier.getBusinessName());
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final.");
        }
    }
}
