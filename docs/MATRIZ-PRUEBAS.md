# Matriz de pruebas funcionales

**Proyecto:** Inventario y flujo de caja  
**Regla de registro:** una prueba solo se marca “Aprobada” cuando se ejecutó el flujo y hay resultado/evidencia. Una prueba de servicio/API no demuestra por sí sola que la pantalla React funciona correctamente.

| ID | Prueba | Resultado esperado | Estado actual | Evidencia / siguiente acción |
|---|---|---|---|---|
| P01 | Abrir dashboard | Los KPI visibles coinciden con inventario y `/api/cash-flow/summary`; se aclara que las cifras de caja son ficticias | **Aprobada**: recorrido manual confirmado por el usuario | [Captura dashboard](evidencias/P01-dashboard/P01-dashboard-1440.png); valores contrastados con los resúmenes API durante la validación |
| P02 | Consultar catálogo | Tabla muestra productos reales del backend y campos legibles | **Aprobada**: recorrido manual confirmado por el usuario | [Captura catálogo](evidencias/P02-catalogo/P02-catalogo-1440.png); datos contrastados con la API durante la validación |
| P03 | Filtrar productos críticos | Solo productos con `stockCurrent <= stockMinimum` | **Aprobada**: filtro y regla verificados manualmente, confirmado por el usuario | [Captura filtro crítico](evidencias/P03-criticos/P03-criticos-1440.png) |
| P04 | Filtrar productos con más de 90 días | Solo productos con `daysInInventory > 90` | **Aprobada**: umbral estricto verificado manualmente, confirmado por el usuario | [Captura filtro >90](evidencias/P04-antiguos/P04-antiguos-criticos-1440.png) |
| P05 | Consultar proveedores | Listado presenta datos relevantes de proveedores | **Aprobada**: listado y datos verificados manualmente, confirmado por el usuario | [Captura directorio](evidencias/P05-proveedores/P05-proveedores-1440.png) |
| P06 | Consultar sugerencias y ofertas | Cantidad y proveedor se calculan con stock/ofertas; se puede comparar | **Aprobada**: sugerencia y comparación verificadas manualmente, confirmado por el usuario | [Sugerencias](evidencias/P06-sugerencias-ofertas/P06-sugerencias-1440.png) y [comparación](evidencias/P06-sugerencias-ofertas/P06-comparacion-ofertas-1440.png) |
| P07 | Datos vacíos o error API | Mensaje útil; aplicación no se bloquea y permite reintentar | **Aprobada**: error, recuperación y estado vacío verificados manualmente, confirmado por el usuario | [Captura error API](evidencias/P07-error-vacio/P07-error-api-1440.png) |
| P08 | Validar formularios | Obligatorios y valores inválidos se rechazan en cliente/servidor con mensajes comprensibles | **Aprobada**: validaciones y CRUD probados manualmente, confirmado por el usuario | [Captura campo obligatorio](evidencias/P08-formularios/P08-obligatorios-proveedor-1440.png) |
| P09 | Reiniciar aplicación/H2 | Se documenta que H2 en memoria reinicializa datos al reiniciar | **Aprobada**: reinicio y recarga de semilla verificados manualmente, confirmado por el usuario | [Evidencia de reinicio](evidencias/P09-reinicio-h2/Reinicio%20H2.png) |
| P10 | Diseño adaptable | Navegación y datos legibles en anchos escritorio, tablet y móvil | **Aprobada**: anchos responsive verificados manualmente, confirmado por el usuario | [Captura responsive](evidencias/P10-responsive/P10-proveedores-786x651.png) |
| P11 | Filtrar baja rotación | Stock > 0, edad > 90 días y última venta nula/anterior a hoy − 90 días | **Aprobada**: regla y vista verificadas manualmente, confirmado por el usuario | [Captura de alertas](evidencias/P11-baja-rotacion/Baja%20Rotaci%C3%B3n.png) |
| P12 | Registrar y pagar movimiento/compromiso demo | Ingreso/gasto modifica saldo; compromiso abierto reduce capacidad; pago registra gasto una vez | **Aprobada**: movimientos, compromiso, pago y ausencia de doble conteo verificados manualmente, confirmado por el usuario | [Captura flujo de caja](evidencias/P12-caja-compromisos/Caja%20Compromisos.png) |

## Registro de defectos

No se reportaron defectos bloqueantes durante la validación manual P01–P12 confirmada por el usuario. Si se detecta un defecto posteriormente, registrar cada caso con los campos siguientes:

| Campo | Registro |
|---|---|
| ID de prueba | N/A — sin defectos reportados |
| Pasos para reproducir | N/A |
| Resultado actual | N/A |
| Resultado esperado | N/A |
| Captura o log | N/A |
| Hipótesis | N/A |
| Corrección aplicada | N/A |
| Reprueba / estado final | N/A |

## Verificaciones automatizadas ya registradas

- Backend: `./mvnw.cmd clean test` — 12 pruebas de integración aprobadas: fixtures, filtros, baja rotación, sugerencias, métricas de dashboard, movimientos/compromisos, pago sin doble conteo y endpoints HTTP de consulta/creación/pago.
- JavaDoc: `./mvnw.cmd javadoc:javadoc` — generación completada en la última ejecución registrada.
- Frontend: `npm.cmd run lint` — aprobado.
- Frontend: `npm.cmd run build` — aprobado.
- Smoke test local anterior: dashboard, catálogo filtrado, proveedores, sugerencias y CORS respondieron en desarrollo.

Las pruebas automatizadas y los recorridos manuales son verificaciones distintas. P01–P12 se registran aprobadas según la validación manual informada por el usuario; las rutas enlazan las capturas disponibles en `docs/evidencias/`.
