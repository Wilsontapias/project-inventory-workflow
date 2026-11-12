# Evidencias de pruebas manuales — Inventario y flujo de caja

Esta carpeta contiene las capturas y artefactos que sustentan las pruebas **P01–P12** de `docs/MATRIZ-PRUEBAS.md`.  
Una prueba solo se marca “Aprobada” cuando aquí existe el resultado/evidencia correspondiente.

## Estructura

| Carpeta | Prueba | Contenido esperado |
|---|---|---|
| `P01-dashboard/` | P01 Abrir dashboard | Captura del dashboard + JSON de `/api/dashboard/summary` y `/api/cash-flow/summary` |
| `P02-catalogo/` | P02 Consultar catálogo | Captura del catálogo + dato comparado con la respuesta API |
| `P03-criticos/` | P03 Filtrar productos críticos | URL/filtro, conteo y captura (`status=CRITICAL`) |
| `P04-antiguos/` | P04 Filtrar > 90 días | Captura con `olderThanDays=90`; verificar que 90 exactos no entren |
| `P05-proveedores/` | P05 Consultar proveedores | Captura del directorio + respuesta de `/api/suppliers` |
| `P06-sugerencias-ofertas/` | P06 Sugerencias y ofertas | Captura de sugerencia + modal de comparación de ofertas |
| `P07-error-vacio/` | P07 Datos vacíos o error API | Mensaje de error/vacío y recuperación (reintentar) |
| `P08-formularios/` | P08 Validar formularios | Capturas de rechazos (SKU duplicado, mínimo > ideal, correo inválido, cupo usado > límite) y CRUD exitoso |
| `P09-reinicio-h2/` | P09 Reiniciar H2 | Evidencia suministrada y validada por el usuario del reinicio y la carga de datos semilla |
| `P10-responsive/` | P10 Diseño adaptable | Capturas a 1440 px, 768 px y 390 px; defectos anotados |
| `P11-baja-rotacion/` | P11 Filtrar baja rotación | Captura con `lowRotation=true` y tabla de alertas |
| `P12-caja-compromisos/` | P12 Movimiento/compromiso demo | Capturas de saldo/capacidad antes y después; pago sin doble conteo |

## Convención de nombres

- Capturas: `Pxx-descripcion-ancho.png` (ej. `P01-dashboard-1440.png`, `P10-responsive-390.png`).
- JSON opcionales: `Pxx-endpoint.json` dentro de la carpeta de la prueba correspondiente.
- Defectos: `defectos.md` dentro de la carpeta correspondiente.

## Capturas ya clasificadas

| Archivo | Qué muestra |
|---|---|
| `P01-dashboard/P01-dashboard-1440.png` | Dashboard, datos ficticios y solicitudes a la API |
| `P02-catalogo/P02-catalogo-1440.png` | Catálogo conectado al backend |
| `P03-criticos/P03-criticos-1440.png` | Filtro de productos críticos |
| `P04-antiguos/P04-antiguos-criticos-1440.png` | Filtros crítico y antigüedad >90 días |
| `P05-proveedores/P05-proveedores-1440.png` | Directorio de proveedores |
| `P06-sugerencias-ofertas/P06-sugerencias-1440.png` | Lista de sugerencias |
| `P06-sugerencias-ofertas/P06-comparacion-ofertas-1440.png` | Modal de comparación de ofertas |
| `P07-error-vacio/P07-error-api-1440.png` | Error de conexión y botón para reintentar |
| `P08-formularios/P08-obligatorios-proveedor-1440.png` | Validación de campo obligatorio |
| `P09-reinicio-h2/Reinicio H2.png` | Evidencia de P09, validada por el usuario |
| `P10-responsive/P10-proveedores-786x651.png` | Vista de proveedores en viewport 786×651 |
| `P11-baja-rotacion/Baja Rotación.png` | Alertas de inventario y productos de baja rotación |
| `P12-caja-compromisos/Caja Compromisos.png` | Resumen de flujo de caja y compromisos |

Las capturas documentan el recorrido. El usuario confirmó que ejecutó y aprobó manualmente los criterios de P01–P12; consulta `docs/MATRIZ-PRUEBAS.md` para los resultados registrados. Algunas capturas son representativas y no muestran por sí solas cada paso del flujo.

## Regla

No marcar una prueba como “Aprobada” en `docs/MATRIZ-PRUEBAS.md` sin su evidencia en esta carpeta. Las verificaciones automáticas (backend, JavaDoc, lint, build) no sustituyen el recorrido visual.
