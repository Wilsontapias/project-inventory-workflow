package com.tdea.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import com.tdea.backend.api.dto.product.ProductResponse;
import com.tdea.backend.api.dto.purchase.PurchaseSuggestionResponse;
import com.tdea.backend.api.dto.cashflow.CashMovementRequest;
import com.tdea.backend.api.dto.cashflow.PurchaseCommitmentRequest;
import com.tdea.backend.domain.enums.CashMovementType;
import com.tdea.backend.domain.enums.ProductStatus;
import com.tdea.backend.repository.ProductRepository;
import com.tdea.backend.repository.CashMovementRepository;
import com.tdea.backend.repository.PurchaseCommitmentRepository;
import com.tdea.backend.repository.SupplierOfferRepository;
import com.tdea.backend.repository.SupplierRepository;
import com.tdea.backend.service.CashFlowService;
import com.tdea.backend.service.DashboardService;
import com.tdea.backend.service.ProductService;
import com.tdea.backend.service.PurchaseSuggestionService;

/** Pruebas de integración para el arranque y los casos principales del backend. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierOfferRepository supplierOfferRepository;

    @Autowired
    private CashMovementRepository cashMovementRepository;

    @Autowired
    private PurchaseCommitmentRepository commitmentRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseSuggestionService purchaseSuggestionService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CashFlowService cashFlowService;

    @Autowired
    private Environment environment;

    @Test
    void contextLoadsAndSeedsDemoData() {
        assertThat(productRepository.count()).isEqualTo(20);
        assertThat(supplierRepository.count()).isEqualTo(5);
        assertThat(supplierOfferRepository.count()).isEqualTo(40);
        assertThat(cashMovementRepository.count()).isEqualTo(4);
        assertThat(commitmentRepository.count()).isEqualTo(3);
    }

    @Test
    void criticalAndAgingFiltersUseStrictDocumentedRules() {
        List<ProductResponse> criticalProducts = productService.findAll(null, null, ProductStatus.CRITICAL, null, null);
        List<ProductResponse> agingProducts = productService.findAll(null, null, null, 90, null);

        assertThat(criticalProducts).isNotEmpty()
                .allSatisfy(product -> assertThat(product.stockCurrent()).isLessThanOrEqualTo(product.stockMinimum()));
        assertThat(agingProducts).isNotEmpty()
                .allSatisfy(product -> assertThat(product.daysInInventory()).isGreaterThan(90));
    }

    @Test
    void lowRotationRequiresInventoryOlderThan90DaysAndNoSaleInTheLast90Days() {
        List<ProductResponse> lowRotationProducts = productService.findAll(null, null, null, null, true);

        assertThat(lowRotationProducts).isNotEmpty().allSatisfy(product -> {
            assertThat(product.daysInInventory()).isGreaterThan(90);
            assertThat(product.lowRotation()).isTrue();
        });
    }

    @Test
    void purchaseSuggestionsAreCalculatedFromStockAndOffers() {
        List<PurchaseSuggestionResponse> suggestions = purchaseSuggestionService.findAll(null);

        assertThat(suggestions).isNotEmpty().allSatisfy(suggestion -> {
            assertThat(suggestion.stockCurrent()).isLessThanOrEqualTo(suggestion.stockMinimum());
            assertThat(suggestion.suggestedQuantity())
                    .isEqualTo(suggestion.stockIdeal() - suggestion.stockCurrent());
            assertThat(suggestion.supplierAlternatives()).hasSize(2);
            assertThat(suggestion.estimatedTotal()).isPositive();
        });
    }

    @Test
    void dashboardMetricsAreDerivedFromDemoEntities() {
        var summary = dashboardService.getSummary();

        assertThat(summary.productCount()).isEqualTo(20);
        assertThat(summary.supplierCount()).isEqualTo(5);
        assertThat(summary.criticalProductCount()).isPositive();
        assertThat(summary.productsOlderThan90Days()).isPositive();
        assertThat(summary.inventoryCostValueCop()).isPositive();
        assertThat(summary.suggestedReorderValueCop()).isPositive();
        assertThat(summary.financialDataNote()).contains("datos ficticios");
    }

    @Test
    void cashFlowSummaryCalculatesBalanceAndNetReorderCapacity() {
        var summary = cashFlowService.getSummary(null, null);

        assertThat(summary.openingBalanceCop()).isEqualByComparingTo("320000000.00");
        assertThat(summary.cashBalanceCop()).isEqualByComparingTo("369000000.00");
        assertThat(summary.periodIncomeCop()).isEqualByComparingTo("93000000.00");
        assertThat(summary.periodExpensesCop()).isEqualByComparingTo("44000000.00");
        assertThat(summary.commitmentsDueNext30DaysCop()).isEqualByComparingTo("98000000.00");
        assertThat(summary.availableForReorderCop()).isEqualByComparingTo("271000000.00");
        assertThat(summary.dataNote()).contains("ficticios");
    }

    @Test
    @Transactional
    void creatingCashMovementUpdatesCurrentBalance() {
        var before = cashFlowService.getSummary(null, null);
        CashMovementRequest request = new CashMovementRequest(LocalDate.now(), CashMovementType.INCOME,
                "Ingreso de prueba", "Pruebas", new BigDecimal("1250000"));

        var created = cashFlowService.createMovement(request);
        var after = cashFlowService.getSummary(null, null);

        assertThat(created.type()).isEqualTo(CashMovementType.INCOME);
        assertThat(after.cashBalanceCop()).isEqualByComparingTo(
                before.cashBalanceCop().add(new BigDecimal("1250000")));
    }

    @Test
    @Transactional
    void creatingCommitmentReducesReorderCapacity() {
        var before = cashFlowService.getSummary(null, null);
        PurchaseCommitmentRequest request = new PurchaseCommitmentRequest(LocalDate.now().plusDays(10),
                "Compromiso de prueba", new BigDecimal("5000000"), null);

        var created = cashFlowService.createCommitment(request);
        var after = cashFlowService.getSummary(null, null);

        assertThat(created.status()).isEqualTo(com.tdea.backend.domain.enums.CommitmentStatus.OPEN);
        assertThat(after.availableForReorderCop())
                .isEqualByComparingTo(before.availableForReorderCop().subtract(new BigDecimal("5000000")));
    }

    @Test
    @Transactional
    void payingCommitmentCreatesExpenseAndDoesNotDoubleCountIt() {
        var commitment = cashFlowService.findCommitments(com.tdea.backend.domain.enums.CommitmentStatus.OPEN).get(0);
        var before = cashFlowService.getSummary(null, null);

        var paid = cashFlowService.markCommitmentPaid(commitment.id());
        var after = cashFlowService.getSummary(null, null);

        assertThat(paid.status()).isEqualTo(com.tdea.backend.domain.enums.CommitmentStatus.PAID);
        assertThat(after.cashBalanceCop()).isEqualByComparingTo(
                before.cashBalanceCop().subtract(commitment.amount()));
        assertThat(after.availableForReorderCop()).isEqualByComparingTo(before.availableForReorderCop());
    }

    @Test
    void dashboardEndpointReturnsTheSeededSummary() throws Exception {
        int port = Integer.parseInt(environment.getProperty("local.server.port"));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/dashboard/summary"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"productCount\":20", "\"supplierCount\":5");
    }

    @Test
    void cashFlowEndpointReturnsFictionalSeededData() throws Exception {
        int port = Integer.parseInt(environment.getProperty("local.server.port"));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/cash-flow/summary"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"openingBalanceCop\":320000000",
                "\"availableForReorderCop\":271000000.00", "ficticios");
    }

    @Test
    void cashFlowMutationEndpointsCreateMovementAndPayCommitment() throws Exception {
        int port = Integer.parseInt(environment.getProperty("local.server.port"));
        HttpClient client = HttpClient.newHttpClient();
        Long createdMovementId = null;
        Long createdCommitmentId = null;
        String commitmentDescription = "API test commitment";

        try {
            String movementJson = "{\"movementDate\":\"" + LocalDate.now()
                    + "\",\"type\":\"INCOME\",\"description\":\"API test income\","
                    + "\"category\":\"Test\",\"amount\":1250000}";
            HttpRequest movementHttpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/cash-flow/movements"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(movementJson))
                    .build();
            HttpResponse<String> movementHttpResponse = client.send(movementHttpRequest,
                    HttpResponse.BodyHandlers.ofString());
            assertThat(movementHttpResponse.statusCode()).isEqualTo(201);
            assertThat(movementHttpResponse.body()).contains("\"type\":\"INCOME\"", "\"amount\":1250000");
            createdMovementId = findId(movementHttpResponse.body());
            HttpRequest findMovementRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/cash-flow/movements/" + createdMovementId))
                    .GET().build();
            assertThat(client.send(findMovementRequest, HttpResponse.BodyHandlers.ofString()).statusCode()).isEqualTo(200);

            String commitmentJson = "{\"dueDate\":\"" + LocalDate.now().plusDays(10)
                    + "\",\"description\":\"" + commitmentDescription
                    + "\",\"amount\":2750000,\"supplierId\":null}";
            HttpRequest commitmentHttpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/cash-flow/commitments"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(commitmentJson))
                    .build();
            HttpResponse<String> commitmentHttpResponse = client.send(commitmentHttpRequest,
                    HttpResponse.BodyHandlers.ofString());
            assertThat(commitmentHttpResponse.statusCode()).isEqualTo(201);
            assertThat(commitmentHttpResponse.body()).contains("\"status\":\"OPEN\"");
            createdCommitmentId = findId(commitmentHttpResponse.body());
            HttpRequest findCommitmentRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/cash-flow/commitments/" + createdCommitmentId))
                    .GET().build();
            assertThat(client.send(findCommitmentRequest, HttpResponse.BodyHandlers.ofString()).statusCode()).isEqualTo(200);

            HttpRequest payRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/cash-flow/commitments/"
                            + createdCommitmentId + "/pay"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> payResponse = client.send(payRequest, HttpResponse.BodyHandlers.ofString());
            assertThat(payResponse.statusCode()).isEqualTo(200);
            assertThat(payResponse.body()).contains("\"status\":\"PAID\"");
        } finally {
            if (createdMovementId != null) {
                cashMovementRepository.deleteById(createdMovementId);
            }
            cashMovementRepository.findAll().stream()
                    .filter(movement -> movement.getDescription().equals("Pago de compromiso: " + commitmentDescription))
                    .forEach(cashMovementRepository::delete);
            if (createdCommitmentId != null) {
                commitmentRepository.deleteById(createdCommitmentId);
            }
        }
    }

    private Long findId(String json) {
        Matcher matcher = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\d+)").matcher(json);
        if (!matcher.find()) {
            throw new AssertionError("La respuesta JSON no contiene un id: " + json);
        }
        return Long.parseLong(matcher.group(1));
    }
}
