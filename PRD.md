# PRD — Inventario y flujo de caja

| Campo | Valor |
|---|---|
| Estado | Aprobado por el responsable del proyecto; sujeto a validación del docente/equipo si aplica |
| Versión | 0.1 |
| Caso de estudio | Comercializadora de suministros informáticos |
| Referencias | `Guia_Laboratorio_IA_Inventario_Flujo_Caja.pdf`, exportación Stitch en `stitch_inventario_y_flujo_de_caja/` |
| Prototipo relacionado | Backend Spring Boot + H2 y frontend React |

> El responsable del proyecto aprobó este PRD para continuar la implementación. El docente/equipo pueden requerir validación adicional.

## 1. Resumen

La empresa vende computadores, portátiles, servidores, UPS, componentes, accesorios y software. Al cierre de cada mes necesita identificar existencias críticas o antiguas, comparar proveedores y estimar el costo de reabastecimiento. El prototipo ofrece un catálogo y recomendaciones explicables para apoyar esas decisiones.

**Límite importante de la versión actual:** el prototipo registra movimientos y compromisos **ficticios** para demostrar un flujo básico; no representa caja, contabilidad, ventas ni obligaciones reales de la empresa. La recomendación financiera es solo orientativa.

## 2. Problema y oportunidad

La revisión manual de existencias y cotizaciones dificulta detectar quiebres de stock y productos que llevan demasiado tiempo almacenados. Sin una vista consolidada, el responsable puede pedir tarde, pedir cantidades inadecuadas o comparar proveedores sin considerar precio, disponibilidad y plazo.

Una solución pequeña, basada en reglas explícitas y supervisada por una persona, puede consolidar los datos disponibles y justificar las recomendaciones. El prototipo no sustituye la decisión humana ni un sistema contable.

## 3. Usuarios

- **Propietario o administrador del negocio (usuario principal):** revisa el inventario, las alertas y el costo estimado antes de decidir si inicia una compra.
- **Responsable de inventario/abastecimiento:** consulta productos, actualiza sus datos, compara ofertas y mantiene el directorio de proveedores.

La primera versión no autentica ni diferencia permisos; se trata de una demostración local con datos ficticios.

## 4. Objetivos y métricas de éxito

### Objetivos

1. Consultar existencias, proveedor principal, costo, precio, mínimos, ideales y antigüedad en una interfaz común.
2. Detectar productos bajo el mínimo y productos con más de 90 días en inventario.
3. Recomendar una cantidad de reabastecimiento y mostrar la regla y las ofertas que sustentan la recomendación.
4. Mostrar movimientos de caja y compromisos ficticios para ilustrar la capacidad de reorden, sin presentarlos como saldos reales.
5. Consultar condiciones comerciales de proveedores sin almacenar datos bancarios.
6. Conservar trazabilidad entre requisitos, mockups, API y pantallas.

### Métricas propuestas

La guía no define metas numéricas. El equipo debe establecer línea base, período y meta antes de medir.

| Métrica | Método de medición | Estado |
|---|---|---|
| Tiempo de consolidación del cierre | Minutos desde iniciar la revisión hasta consolidar inventario y alertas; comparar con el proceso manual | Meta por acordar; no medible con caja real en esta versión |
| Quiebres en productos de alta demanda | Conteo de eventos de stock cero en un período dividido por productos de alta demanda | Requiere historial de movimientos/ventas |
| Órdenes originadas por recomendaciones | Órdenes aceptadas desde sugerencias / total de órdenes | Requiere registrar y aprobar órdenes; no implementado |
| Exactitud de sugerencia | Diferencia entre sugerido y cantidad finalmente aprobada/recibida | Requiere historial de compras |

No se debe afirmar que estas métricas mejoraron sin mediciones y evidencias reales.

## 5. Alcance del MVP

### Incluido en el prototipo actual

- Dashboard con número de productos/proveedores, críticos, productos con más de 90 días, valoración del inventario al costo y costo estimado de reabastecimiento.
- Resumen de caja demostrativo: saldo inicial configurado en COP 320.000.000, ingresos/gastos ficticios del período, compromisos impagos a 30 días y capacidad neta estimada para reorden.
- Registro de movimientos de ingreso/gasto y compromisos ficticios; al marcar un compromiso como pagado se registra una única salida de caja.
- Catálogo con SKU, nombre, marca, modelo, categoría, subcategoría, costo, precio, existencias, antigüedad, demanda mensual y proveedor principal.
- Búsqueda por SKU/nombre/marca y filtros por categoría, estado y antigüedad.
- Alta, consulta, edición y eliminación de productos, con validación de servidor.
- Vista de productos críticos y antiguos.
- Directorio de proveedores con categoría, ciudad, contacto comercial, plazo de pago, lead time, cupo ficticio, desempeño y marcas representadas.
- Alta, consulta y edición de proveedor. La eliminación se permite solo si no hay producto, oferta ni compromiso asociado.
- Comparación de ofertas por precio unitario, disponibilidad, plazo de entrega y plazo de pago.
- Sugerencias calculadas en el backend; se presenta su justificación y alternativas. No se crea una orden real.
- Alerta de baja rotación: producto con stock disponible, más de 90 días en inventario y sin ventas registradas en los últimos 90 días.
- Datos ficticios en H2 en memoria y manejo de carga, error y vacío en el frontend.

### No incluido

- Flujo de caja real, contabilidad completa, cuentas por pagar/cobrar reales o saldo bancario conciliado. El flujo incluido solo usa fixtures ficticios.
- Ventas, movimientos históricos de inventario, cálculo de rotación basado en transacciones o predicción de demanda.
- Creación, aprobación, recepción o seguimiento de órdenes de compra reales.
- Facturación electrónica, integración tributaria, comercio electrónico/pasarelas y nómina.
- Autenticación y autorización.
- Persistencia durable: H2 en memoria se reinicializa al reiniciar.

## 6. Requisitos funcionales

Prioridad **P0**: parte esencial de la demostración. **P1**: mejora posterior o brecha a decidir. Los requisitos P0 reflejan el prototipo existente; el estado no sustituye las pruebas de aceptación.

| ID | Prioridad | Requisito | Criterio de aceptación | Estado actual |
|---|---|---|---|---|
| RF-01 | P0 | Consultar el resumen del dashboard desde la API | Los indicadores visibles coinciden con `GET /api/dashboard/summary` y `GET /api/cash-flow/summary`; se advierte que las cifras son demostrativas | Implementado; falta recorrido visual documentado |
| RF-02 | P0 | Consultar y mantener productos | La API valida SKU único, campos obligatorios, montos no negativos y relación con proveedor | Implementado; pruebas manuales CRUD pendientes |
| RF-03 | P0 | Filtrar catálogo | Búsqueda y filtros categoría/estado/antigüedad/baja rotación cambian los resultados | Implementado; verificación UI pendiente |
| RF-04 | P0 | Identificar inventario crítico | Un producto es crítico si `stockCurrent <= stockMinimum`; la condición aparece de forma legible | Implementado; prueba de UI pendiente |
| RF-05 | P0 | Identificar productos antiguos | `olderThanDays=90` devuelve solo productos con `daysInInventory > 90` | Implementado; prueba de UI pendiente |
| RF-06 | P0 | Consultar y mantener proveedores | Se validan NIT/RUT único, correo, plazos, cupo y desempeño; se muestra crédito disponible derivado | Implementado; pruebas manuales pendientes |
| RF-07 | P0 | Comparar ofertas por producto | Se muestran precio, disponibilidad, plazo de entrega, crédito y costo total para cantidad sugerida | Implementado; recorrido UI pendiente |
| RF-08 | P0 | Sugerir reabastecimiento | Para producto crítico con stock menor al ideal, cantidad = `stockIdeal - stockCurrent`; proveedor y motivo se explican | Implementado; prueba UI pendiente |
| RF-09 | P0 | Manejar estados de interfaz | Carga, error de conexión, validación y lista vacía son visibles y permiten continuar/reintentar | Parcialmente implementado; verificación manual pendiente |
| RF-10 | P0 | Mostrar valores en COP | Los valores usan formato local COP y se describen como ficticios/estimados cuando aplica | Implementado |
| RF-11 | P0 | Consultar flujo de caja demostrativo | Saldo = COP 320 M inicial + ingresos registrados − gastos registrados; resumen muestra período y nota de datos ficticios | Implementado; prueba visual pendiente |
| RF-12 | P0 | Gestionar compromisos de pago demostrativos | Crear/listar compromisos; capacidad para reorden resta compromisos abiertos con vencimiento entre hoy y hoy + 30 días; pagar registra un gasto una sola vez | Implementado; prueba manual pendiente |
| RF-13 | P0 | Identificar baja rotación | Stock actual > 0, `daysInInventory > 90` y última venta nula o anterior a hoy − 90 días | Implementado con fecha de última venta ficticia; prueba UI pendiente |
| RF-14 | P1 | Registrar órdenes de compra reales | Convertir sugerencia aceptada en orden con estado, proveedor, líneas, costos y fechas | No implementado |
| RF-15 | P1 | Medir rotación/demanda desde historial | Derivar demanda y rotación de transacciones de venta fechadas | No implementado; `lastSaleDate` y demanda actual son datos de demostración |

## 7. Reglas de negocio vigentes

1. **Crítico:** `stockCurrent <= stockMinimum`.
2. **Antigüedad:** el filtro es estricto; más de 90 días significa `daysInInventory > 90`.
3. **Estado:** `CRITICAL` si actual ≤ mínimo; `OVERSTOCK` si actual > ideal; en otro caso `OPTIMAL`.
4. **Cantidad sugerida:** `max(stockIdeal - stockCurrent, 0)`. Solo se genera para producto crítico y con stock actual menor al ideal.
5. **Proveedor recomendado:** entre las ofertas con disponibilidad suficiente, menor costo unitario; en empate, menor lead time. Si ninguna cubre toda la cantidad, se muestra la alternativa de menor costo y una advertencia.
6. **Costo de inventario:** `unitCost × stockCurrent`.
7. **Valor de sugerencia:** cantidad sugerida multiplicada por el costo unitario de la oferta seleccionada.
8. **Saldo demostrativo:** `saldo inicial COP 320.000.000 + movimientos INCOME - movimientos EXPENSE` hasta hoy.
9. **Capacidad demostrativa para reorden:** saldo actual − compromisos `OPEN` con vencimiento entre hoy y hoy + 30 días. Puede ser negativa; no se limita silenciosamente a cero.
10. **Baja rotación:** stock actual > 0, edad estrictamente > 90 días y `lastSaleDate` ausente o estrictamente anterior a la fecha de corte menos 90 días.
11. El cupo de crédito del proveedor no equivale al dinero disponible de la empresa. Ningún valor seeded representa dinero real.

## 8. Flujos principales

### Revisar estado del inventario

1. El responsable abre el dashboard.
2. La aplicación consulta resumen, catálogo y sugerencias a la API.
3. Si hay productos críticos o con más de 90 días, se muestran sus conteos y valor al costo; el usuario abre alertas.
4. El usuario inspecciona stock, antigüedad, costo y proveedor en el catálogo.
5. Si falla la API, se muestra un error de conexión y una opción de reintento; no se deben presentar datos de ejemplo como si fueran actuales.

### Evaluar sugerencia de reabastecimiento

1. El usuario abre “Órdenes sugeridas”.
2. La API devuelve productos bajo mínimo, cantidad sugerida, regla, proveedor recomendado y alternativas.
3. El usuario compara costo, disponibilidad y plazos.
4. La aplicación presenta la recomendación, pero no crea ni aprueba una orden.

### Mantener producto o proveedor

1. El usuario abre el catálogo o directorio y elige agregar/editar.
2. La interfaz valida campos básicos y envía el DTO correspondiente.
3. El servidor aplica validaciones y reglas de unicidad/relación.
4. La interfaz muestra confirmación o errores por campo/mensaje del servidor.

### Revisar flujo de caja demostrativo

1. El usuario abre el módulo de flujo y selecciona un período (por defecto, mes actual hasta hoy).
2. La API devuelve saldo inicial, ingresos, gastos, flujo neto, saldo actual, compromisos próximos y capacidad neta de reorden.
3. El usuario puede registrar movimientos o compromisos ficticios.
4. Al marcar un compromiso abierto como pagado, el sistema cambia su estado y crea un gasto fechado hoy dentro de la misma transacción; no se resta dos veces.
5. El sistema compara las sugerencias de compra con la capacidad de reorden y muestra si caben en la capacidad demo.

## 9. Requisitos no funcionales y criterios propuestos

| ID | Criterio verificable | Estado |
|---|---|---|
| RNF-01 | Las vistas permanecen legibles entre 360 px y escritorio; verificar en navegador y registrar capturas | Validado manualmente según P10; capturas en `docs/evidencias/P10-responsive/` |
| RNF-02 | Navegación y formularios operables con teclado; controles tienen etiquetas y foco visible | Validado manualmente por el responsable |
| RNF-03 | En dataset de demostración local (20 productos), endpoints de lectura responden en ≤2 s en ejecución normal; registrar tiempos si se usa como criterio del equipo | Propuesta; no medido formalmente |
| RNF-04 | Errores REST no exponen trazas internas; validaciones de negocio se aplican en servidor | Implementado en estructura; revisar casos límite |
| RNF-05 | Datos monetarios usan `BigDecimal` en backend y formato COP en frontend | Implementado |
| RNF-06 | No se usan credenciales ni datos personales/comerciales reales; sin autenticación, no apto para producción | Condición del prototipo |
| RNF-07 | El inicio del backend carga datos ficticios y el reinicio puede recrearlos; documentar la pérdida de cambios de H2 | Validado manualmente según P09; evidencia en `docs/evidencias/P09-reinicio-h2/` |
| RNF-08 | Código organizado por capas y DTOs; funciones y reglas públicas documentadas con JavaDoc | JavaDoc generado y revisado manualmente por el responsable |

## 10. Supuestos y decisiones por aprobar

- El responsable del proyecto aprobó estas reglas; docente/equipo pueden requerir validación adicional.
- La frase “20 datos de prueba” se interpreta como **20 productos**, además de 5 proveedores, 40 ofertas, 4 movimientos y 3 compromisos auxiliares. Confirmar con el docente si se refería a 20 registros totales.
- `daysInInventory` y `averageMonthlyDemand` son valores de demostración; no se derivan de movimientos históricos.
- Los datos de proveedores y ofertas son ficticios. Los emails de dominio `example.invalid` son placeholders.
- El saldo inicial de COP 320 M, movimientos y compromisos son exclusivamente fixtures de demostración; sustituirlos por configuración autorizada solo si se acuerda.
- Definir metas numéricas y línea base de las métricas de éxito antes de afirmar mejoras.

## 11. Criterios de aceptación del MVP

- [x] Responsable del proyecto revisó y aprobó este PRD y las reglas; validación docente/equipo pendiente si aplica.
- [x] Las vistas implementadas corresponden a los módulos de este documento y a mockups aprobados; recorrido manual confirmado por el responsable.
- [x] Dashboard, catálogo, alertas, proveedores y sugerencias consumen API real; recorrido manual confirmado por el responsable.
- [x] Filtros respetan los umbrales definidos; formularios muestran errores útiles; validado manualmente.
- [x] Las sugerencias indican cantidad, proveedor, costo y justificación; validado manualmente.
- [x] Se informa explícitamente que saldo, movimientos y compromisos son ficticios, y que no hay órdenes aprobadas ni contabilidad real.
- [x] Matriz de pruebas contiene resultados manuales confirmados y enlaces a evidencia; no se presentan pruebas no ejecutadas como aprobadas.
- [x] README permite reproducir la ejecución y documenta limitaciones de H2 y seguridad.
