package com.tdea.backend.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tdea.backend.domain.entity.CashMovement;
import com.tdea.backend.domain.entity.Product;
import com.tdea.backend.domain.entity.PurchaseCommitment;
import com.tdea.backend.domain.entity.Supplier;
import com.tdea.backend.domain.entity.SupplierOffer;
import com.tdea.backend.domain.enums.CashMovementType;
import com.tdea.backend.domain.enums.ProductCategory;
import com.tdea.backend.domain.enums.SupplierCategory;
import com.tdea.backend.repository.CashMovementRepository;
import com.tdea.backend.repository.ProductRepository;
import com.tdea.backend.repository.PurchaseCommitmentRepository;
import com.tdea.backend.repository.SupplierOfferRepository;
import com.tdea.backend.repository.SupplierRepository;

/**
 * Carga datos ficticios para ejecutar y probar el prototipo sin configuración manual.
 *
 * <p>Se crean 20 productos, 5 proveedores, ofertas comparables y movimientos/compromisos
 * financieros de demostración. Los datos se recrean al reiniciar porque H2 es en memoria.</p>
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierOfferRepository supplierOfferRepository;
    private final CashMovementRepository cashMovementRepository;
    private final PurchaseCommitmentRepository commitmentRepository;

    /**
     * Construye el inicializador con los repositorios requeridos.
     *
     * @param productRepository persistencia de productos
     * @param supplierRepository persistencia de proveedores
     * @param supplierOfferRepository persistencia de ofertas
     * @param cashMovementRepository persistencia de movimientos de caja
     * @param commitmentRepository persistencia de compromisos
     */
    public DemoDataInitializer(ProductRepository productRepository, SupplierRepository supplierRepository,
            SupplierOfferRepository supplierOfferRepository, CashMovementRepository cashMovementRepository,
            PurchaseCommitmentRepository commitmentRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.supplierOfferRepository = supplierOfferRepository;
        this.cashMovementRepository = cashMovementRepository;
        this.commitmentRepository = commitmentRepository;
    }

    /**
     * Carga el conjunto de demostración cuando la base de datos está vacía.
     *
     * @param args argumentos de inicio de Spring Boot
     */
    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        List<Supplier> suppliers = supplierRepository.saveAll(createSuppliers());
        List<ProductSeed> seeds = createProducts();
        List<Product> products = seeds.stream().map(seed -> new Product(
                seed.sku(), seed.name(), seed.brand(), seed.model(), seed.category(), seed.subcategory(),
                money(seed.unitCost()), money(seed.salePrice()), seed.stockCurrent(), seed.stockMinimum(),
                seed.stockIdeal(), seed.daysInInventory(), new BigDecimal(seed.monthlyDemand()),
                demoLastSaleDate(seed), suppliers.get(seed.supplierIndex()))).toList();
        products = productRepository.saveAll(products);

        List<SupplierOffer> offers = new ArrayList<>();
        for (int index = 0; index < products.size(); index++) {
            Product product = products.get(index);
            ProductSeed seed = seeds.get(index);
            Supplier primarySupplier = suppliers.get(seed.supplierIndex());
            Supplier alternateSupplier = suppliers.get((seed.supplierIndex() + 1) % suppliers.size());
            int orderQuantity = Math.max(1, seed.stockIdeal() - seed.stockCurrent());

            offers.add(new SupplierOffer(primarySupplier, product, money(seed.unitCost()),
                    orderQuantity + 20, primarySupplier.getLeadTimeBusinessDays(),
                    primarySupplier.getPaymentTermDays()));

            BigDecimal alternatePrice = money(seed.unitCost()).multiply(new BigDecimal("1.04"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            offers.add(new SupplierOffer(alternateSupplier, product, alternatePrice,
                    orderQuantity + 8, alternateSupplier.getLeadTimeBusinessDays(),
                    alternateSupplier.getPaymentTermDays()));
        }
        supplierOfferRepository.saveAll(offers);
        seedCashMovementsAndCommitments(suppliers);
    }

    private void seedCashMovementsAndCommitments(List<Supplier> suppliers) {
        LocalDate today = LocalDate.now();
        cashMovementRepository.saveAll(List.of(
                new CashMovement(daysAgoWithinCurrentMonth(today, 8), CashMovementType.INCOME,
                        "Venta corporativa de equipos", "Ventas", money(55_000_000)),
                new CashMovement(daysAgoWithinCurrentMonth(today, 6), CashMovementType.INCOME,
                        "Servicios y licencias", "Ingresos operacionales", money(38_000_000)),
                new CashMovement(daysAgoWithinCurrentMonth(today, 4), CashMovementType.EXPENSE,
                        "Servicios de bodega", "Operación", money(26_000_000)),
                new CashMovement(daysAgoWithinCurrentMonth(today, 2), CashMovementType.EXPENSE,
                        "Transporte y logística", "Operación", money(18_000_000))));

        commitmentRepository.saveAll(List.of(
                new PurchaseCommitment(today.plusDays(7), "Compromiso de servidores", money(38_000_000), suppliers.get(1)),
                new PurchaseCommitment(today.plusDays(16), "Compromiso de portátiles", money(32_000_000), suppliers.get(0)),
                new PurchaseCommitment(today.plusDays(28), "Compromiso de energía y UPS", money(28_000_000), suppliers.get(3))));
    }

    private LocalDate daysAgoWithinCurrentMonth(LocalDate today, int daysAgo) {
        return today.withDayOfMonth(Math.max(1, today.getDayOfMonth() - daysAgo));
    }

    private LocalDate demoLastSaleDate(ProductSeed seed) {
        LocalDate today = LocalDate.now();
        if (seed.daysInInventory() <= 90) {
            return today.minusDays(Math.max(1, seed.daysInInventory() / 2));
        }
        int pattern = Math.floorMod(seed.sku().hashCode(), 3);
        if (pattern == 0) {
            return null;
        }
        return today.minusDays(pattern == 1 ? 120 : 40);
    }

    private List<Supplier> createSuppliers() {
        return List.of(
                new Supplier("900000001-1", "Ingram Micro Colombia S.A.S.", "Bogotá", "Sede de demostración",
                        "ventas@example.invalid", "+57 601 000 0001", "Contacto Ingram", SupplierCategory.COMPUTERS,
                        45, 8, money(300_000_000), money(164_500_000), new BigDecimal("95.0"), "Lenovo, Dell, HP"),
                new Supplier("900000002-2", "Nexsys de Colombia", "Bogotá", "Sede de demostración",
                        "ventas@example.invalid", "+57 601 000 0002", "Contacto Nexsys", SupplierCategory.SERVERS,
                        30, 10, money(200_000_000), money(88_000_000), new BigDecimal("92.0"), "HPE, Cisco, Microsoft"),
                new Supplier("900000003-3", "MPS Mayorista de Colombia", "Medellín", "Sede de demostración",
                        "ventas@example.invalid", "+57 604 000 0003", "Contacto MPS", SupplierCategory.COMPONENTS,
                        30, 6, money(180_000_000), money(72_300_000), new BigDecimal("90.0"), "Kingston, Intel, AMD"),
                new Supplier("900000004-4", "Anixter Colombia", "Bogotá", "Sede de demostración",
                        "ventas@example.invalid", "+57 601 000 0004", "Contacto Anixter", SupplierCategory.POWER,
                        45, 5, money(120_000_000), money(45_200_000), new BigDecimal("96.0"), "APC, Cisco, Legrand"),
                new Supplier("900000005-5", "Makro Cómputo Colombia S.A.", "Cali", "Sede de demostración",
                        "ventas@example.invalid", "+57 602 000 0005", "Contacto Makro", SupplierCategory.SOFTWARE,
                        30, 7, money(100_000_000), money(47_000_000), new BigDecimal("89.0"), "Microsoft, Adobe, ESET"));
    }

    private List<ProductSeed> createProducts() {
        return List.of(
                new ProductSeed("LAP-LEN-T14G3", "Portátil Lenovo ThinkPad T14 Gen 4", "Lenovo", "T14 Gen 4", ProductCategory.LAPTOPS, "Portátiles empresariales", 5_200_000, 6_400_000, 2, 5, 10, 12, "4.2", 0),
                new ProductSeed("PRT-HPE-PROL", "Servidor HPE ProLiant DL380 Gen10", "HPE", "DL380 Gen10", ProductCategory.SERVERS, "Servidores", 24_450_000, 29_900_000, 1, 2, 4, 43, "1.1", 1),
                new ProductSeed("UPS-APC-SRT5K", "UPS APC Smart-UPS On-Line 5kVA", "APC", "SRT5K", ProductCategory.UPS, "Energía", 5_700_000, 7_100_000, 2, 3, 6, 98, "1.8", 3),
                new ProductSeed("SW-CSCO-C9200", "Switch Cisco Catalyst 9200L 24P PoE+", "Cisco", "C9200L", ProductCategory.COMPONENTS, "Redes", 3_250_000, 4_100_000, 1, 2, 5, 61, "2.4", 1),
                new ProductSeed("RAM-KNG-DDR5", "Memoria Kingston Server Premier 64GB DDR5", "Kingston", "DDR5 64GB", ProductCategory.COMPONENTS, "Memoria", 562_500, 720_000, 4, 6, 14, 105, "3.5", 2),
                new ProductSeed("SRV-DEL-R740-09", "Servidor Dell PowerEdge R740", "Dell", "PowerEdge R740", ProductCategory.SERVERS, "Servidores", 12_000_000, 15_500_000, 4, 2, 5, 112, "0.8", 0),
                new ProductSeed("MEM-KNG-D464-ECC", "Memoria Kingston DDR4 ECC 64GB", "Kingston", "DDR4 ECC", ProductCategory.COMPONENTS, "Memoria", 687_500, 850_000, 8, 4, 16, 105, "2.2", 2),
                new ProductSeed("UPS-APC-RT10K-OL", "UPS APC Smart-UPS RT 10kVA", "APC", "RT10K-OL", ProductCategory.UPS, "Energía", 19_000_000, 23_500_000, 2, 2, 4, 98, "0.7", 3),
                new ProductSeed("NET-CIS-C9300-48P", "Switch Cisco Catalyst 9300 48P", "Cisco", "C9300-48P", ProductCategory.COMPONENTS, "Redes", 13_500_000, 16_900_000, 3, 2, 5, 94, "1.0", 1),
                new ProductSeed("LAP-HP-840G10", "Portátil HP EliteBook 840 G10", "HP", "EliteBook 840 G10", ProductCategory.LAPTOPS, "Portátiles empresariales", 4_800_000, 5_950_000, 0, 3, 8, 20, "3.8", 0),
                new ProductSeed("SSD-SAM-990PRO", "SSD Samsung 990 PRO 2TB", "Samsung", "990 PRO 2TB", ProductCategory.COMPONENTS, "Almacenamiento", 690_000, 890_000, 12, 5, 15, 35, "5.0", 2),
                new ProductSeed("LIC-MIC-M365-BUS", "Licencia Microsoft 365 Business Standard", "Microsoft", "Business Standard", ProductCategory.SOFTWARE, "Productividad", 430_000, 590_000, 7, 4, 12, 48, "6.0", 4),
                new ProductSeed("MOU-LOG-MXMASTER", "Mouse Logitech MX Master 3S", "Logitech", "MX Master 3S", ProductCategory.ACCESSORIES, "Periféricos", 390_000, 520_000, 3, 5, 12, 76, "2.6", 0),
                new ProductSeed("RAM-COR-DDR4-32", "Memoria Corsair DDR4 32GB", "Corsair", "DDR4 32GB", ProductCategory.COMPONENTS, "Memoria", 285_000, 390_000, 9, 4, 14, 22, "4.4", 2),
                new ProductSeed("UPS-APC-BX1500", "UPS APC Back-UPS 1500VA", "APC", "BX1500", ProductCategory.UPS, "Energía", 780_000, 1_050_000, 2, 4, 10, 91, "2.0", 3),
                new ProductSeed("SRV-HPE-DL360", "Servidor HPE ProLiant DL360 Gen10", "HPE", "DL360 Gen10", ProductCategory.SERVERS, "Servidores", 17_800_000, 21_900_000, 3, 2, 5, 68, "0.9", 1),
                new ProductSeed("LIC-ADOBE-CC", "Licencia Adobe Creative Cloud", "Adobe", "Creative Cloud", ProductCategory.SOFTWARE, "Diseño", 1_200_000, 1_550_000, 5, 3, 10, 125, "1.4", 4),
                new ProductSeed("KB-LOG-MXKEYS", "Teclado Logitech MX Keys", "Logitech", "MX Keys", ProductCategory.ACCESSORIES, "Periféricos", 420_000, 560_000, 8, 4, 12, 40, "3.1", 0),
                new ProductSeed("GPU-NVD-RTXA2000", "Tarjeta gráfica NVIDIA RTX A2000", "NVIDIA", "RTX A2000", ProductCategory.COMPONENTS, "Tarjetas gráficas", 2_400_000, 3_050_000, 1, 2, 5, 97, "0.8", 2),
                new ProductSeed("LIC-ESET-ENDPOINT", "Licencia ESET Endpoint Security", "ESET", "Endpoint Security", ProductCategory.SOFTWARE, "Seguridad", 165_000, 240_000, 0, 5, 20, 15, "8.0", 4));
    }

    private BigDecimal money(long amount) {
        return BigDecimal.valueOf(amount).setScale(2);
    }

    private record ProductSeed(String sku, String name, String brand, String model, ProductCategory category,
            String subcategory, long unitCost, long salePrice, int stockCurrent, int stockMinimum,
            int stockIdeal, int daysInInventory, String monthlyDemand, int supplierIndex) {
    }
}
