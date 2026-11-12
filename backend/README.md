# Backend — Inventory Workflow API

API REST para el prototipo de inventario, proveedores y sugerencias de reabastecimiento. Está construido con Java 17, Spring Boot 4.0.9, Spring Data JPA y H2 en memoria.

## Ejecutar y probar

Desde esta carpeta, en Windows:

```powershell
./mvnw.cmd test
./mvnw.cmd spring-boot:run
```

El servidor escucha en `http://localhost:8080` por defecto. Se puede cambiar con `SERVER_PORT`. La consola H2 local está en `http://localhost:8080/h2-console`, con JDBC URL `jdbc:h2:mem:inventorydb`, usuario `sa` y contraseña `sa`. No habilitar ni exponer la consola en producción.

La base es temporal (`create-drop`): cada arranque recrea el esquema y carga datos ficticios. El inicializador carga 20 productos, 5 proveedores, 40 ofertas, 4 movimientos de caja y 3 compromisos.
El saldo inicial ficticio es COP 320.000.000 y se puede cambiar con `CASH_OPENING_BALANCE_COP`.

## Estructura

```text
src/main/java/com/tdea/backend/
├── api/
│   ├── controller/      # Contratos HTTP
│   ├── dto/             # Solicitudes y respuestas REST
│   └── mapper/          # Conversión entidad–DTO
├── common/exception/    # Excepciones de dominio y Problem Details
├── config/              # CORS y carga de datos ficticios
├── domain/
│   ├── entity/          # Product, Supplier, SupplierOffer, CashMovement, PurchaseCommitment
│   └── enums/           # Categorías, estados, movimientos y compromisos
├── repository/          # Repositorios Spring Data JPA
└── service/             # Reglas de negocio
```

## Reglas de negocio implementadas

- Un producto está `CRITICAL` cuando `stockCurrent <= stockMinimum`.
- El filtro de antigüedad usa comparación estricta: `daysInInventory > olderThanDays`; por ejemplo, `olderThanDays=90` no incluye productos de exactamente 90 días.
- Las unidades sugeridas son `max(stockIdeal - stockCurrent, 0)` para productos críticos cuyo stock actual sea menor que el ideal.
- La recomendación elige primero la oferta que pueda cubrir la cantidad sugerida con menor costo unitario; en empate, prefiere menor tiempo de entrega. Si ninguna cubre toda la cantidad, devuelve la alternativa de menor precio y avisa que se debe validar disponibilidad.
- El valor del inventario se calcula como costo de adquisición unitario por existencias actuales.
- Baja rotación requiere stock actual mayor que cero, antigüedad `> 90` y última venta nula o anterior a hoy menos 90 días.
- Saldo de caja demostrativo = saldo inicial COP 320.000.000 + ingresos − gastos registrados hasta hoy.
- Capacidad de reorden demostrativa = saldo actual − compromisos `OPEN` con vencimiento entre hoy y hoy + 30 días. El valor no se recorta a cero.
- Al pagar un compromiso se registra un gasto y se cambia su estado a `PAID` en la misma transacción; no se descuenta dos veces.
- Ninguna cifra representa caja real ni capacidad financiera real. Movimientos, compromisos, costos, ofertas y proveedores son ficticios.
- La categoría de un proveedor/producto se envía como el nombre del enum en mayúsculas (por ejemplo, `SERVERS`, `LAPTOPS`, `COMPUTERS`).

## API disponible

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/dashboard/summary` | Indicadores agregados del inventario |
| `GET` | `/api/products` | Catálogo; filtros opcionales `search`, `category`, `status`, `olderThanDays`, `lowRotation` |
| `GET` | `/api/products/{id}` | Detalle de producto |
| `POST` | `/api/products` | Crear producto; responde `201 Created` |
| `PUT` | `/api/products/{id}` | Actualizar producto |
| `DELETE` | `/api/products/{id}` | Eliminar producto y sus ofertas; responde `204 No Content` |
| `GET` | `/api/suppliers` | Proveedores; filtros `search`, `category`, `paymentTermDays` |
| `GET` | `/api/suppliers/{id}` | Detalle de proveedor |
| `POST` | `/api/suppliers` | Crear proveedor; responde `201 Created` |
| `PUT` | `/api/suppliers/{id}` | Actualizar proveedor |
| `DELETE` | `/api/suppliers/{id}` | Eliminar si no tiene productos/ofertas/compromisos asociados |
| `GET` | `/api/purchase-suggestions` | Recomendaciones calculadas; filtro opcional `search` |
| `GET` | `/api/products/{id}/supplier-offers` | Ofertas comparables para un producto |
| `GET` | `/api/cash-flow/summary` | Resumen por período; filtros opcionales `from`, `to` (`YYYY-MM-DD`) |
| `GET` | `/api/cash-flow/movements` | Ingresos y gastos del período |
| `GET` | `/api/cash-flow/movements/{id}` | Detalle de movimiento |
| `POST` | `/api/cash-flow/movements` | Registrar movimiento `INCOME`/`EXPENSE` |
| `GET` | `/api/cash-flow/commitments` | Compromisos; filtro opcional `status=OPEN|PAID` |
| `GET` | `/api/cash-flow/commitments/{id}` | Detalle de compromiso |
| `POST` | `/api/cash-flow/commitments` | Registrar compromiso pendiente |
| `PATCH` | `/api/cash-flow/commitments/{id}/pay` | Registrar pago de compromiso |

Los cuerpos de creación/actualización usan los campos de `ProductRequest` y `SupplierRequest`. Los errores se devuelven con estructura Problem Details; errores de validación agregan la propiedad `errors` por campo.

## Configuración para React/Vite

Por defecto, CORS permite `http://localhost:5173` y `http://127.0.0.1:5173`. Cambiar los orígenes con `CORS_ALLOWED_ORIGINS`, separados por coma.

## Limitaciones

- Prototipo local sin autenticación ni autorización.
- H2 en memoria no conserva datos después del reinicio.
- Filtros aplicados sobre un conjunto pequeño de datos de demostración; antes de crecer el catálogo se deben trasladar a consultas paginadas en base de datos.
- Movimientos, saldo inicial, compromisos y última venta son datos ficticios; no hay historial completo de ventas ni compras reales.
- No se crean, aprueban ni reciben órdenes de compra reales.
- Las ofertas y los datos de proveedores son ficticios; no usar información bancaria o comercial real.
