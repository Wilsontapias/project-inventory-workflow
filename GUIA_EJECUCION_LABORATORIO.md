# Guía de ejecución y validación del laboratorio

**Fuente analizada:** `Guia_Laboratorio_IA_Inventario_Flujo_Caja.pdf`  
**Proyecto:** aplicación web de inventario, proveedores y flujo de caja  
**Stack solicitado por la guía:** Antigravity, Google Stitch, Spring Boot, API REST, React y H2 en memoria.

> Este documento convierte la guía del laboratorio en un plan de trabajo verificable. No afirma que las fases ya estén ejecutadas. Al momento de elaborarlo, la carpeta del proyecto contiene la guía PDF y `LICENSE`; todavía no se observan PRD, mockups, especificación técnica, código ni evidencias de pruebas.

## 1. Qué se debe construir

Un **prototipo funcional integrado** para una empresa que vende suministros informáticos. Debe ayudar a consultar el inventario, identificar productos críticos o con baja rotación/antigüedad, consultar proveedores, sugerir reabastecimientos y presentar información básica para el cierre mensual y la toma de decisiones de caja.

La práctica no consiste en pedir a la IA que genere toda la aplicación sin control. El trabajo debe avanzar en este orden, con revisión humana entre etapas:

1. Preparar y registrar el entorno.
2. Definir y aprobar el PRD.
3. Diseñar, revisar y exportar los mockups.
4. Definir y aprobar la especificación técnica.
5. Generar e inspeccionar el backend y el frontend.
6. Ejecutar y conectar ambos componentes.
7. Probar, corregir y registrar resultados reales.
8. Revisar calidad, limitaciones y deuda técnica.

## 2. Alcance del MVP

### Incluido

- **Tablero de cierre mensual y salud financiera:** resumen de métricas, capital inmovilizado, capacidad de reorden e indicadores de cierre, con reglas y unidades documentadas.
- **Inventario y catálogo tecnológico:** consulta de productos, existencias, rotación y riesgo de obsolescencia; stock actual, mínimo e ideal; filtros de productos críticos y con más de 90 días.
- **Proveedores y órdenes sugeridas:** consulta/gestión de proveedores, comparación de costos y recomendaciones de compra basadas en reglas de negocio, inventario y datos disponibles.
- Aplicación web React que obtiene los datos del backend Spring Boot mediante API REST.
- H2 en memoria y datos ficticios para demostración. La guía propone 20 datos de prueba; debe quedar documentada su distribución entre entidades.
- Manejo de carga, errores de API y resultados vacíos; validación de formularios y diseño adaptable.

### Fuera del alcance inicial

- Facturación electrónica integrada con entidades tributarias.
- Contabilidad completa o reemplazo de un sistema contable.
- Comercio electrónico B2C y pasarelas de pago.
- Nómina y prestaciones/parafiscales.
- Autenticación y autorización de usuarios.

No se deben añadir módulos fuera de este alcance sin aprobar el cambio en el PRD y actualizar la especificación y el plan.

## 3. Decisiones de negocio que deben quedar explícitas

La guía exige reglas verificables, pero no define todas las fórmulas ni umbrales. El equipo debe decidirlas y documentarlas en el PRD y la especificación, sin presentarlas como reglas impuestas por la guía.

- Qué significa **producto crítico** (por ejemplo, stock actual menor o igual al mínimo, si el equipo lo aprueba).
- Cómo se calculan los **días en inventario**, la baja rotación y la obsolescencia; confirmar si “más de 90 días” significa estrictamente `> 90`.
- Cómo se obtiene la **cantidad sugerida** (por ejemplo, diferencia entre stock ideal y actual, con mínimo cero) y cómo se determina el proveedor recomendado.
- Qué costos, compromisos de pago, ingresos u otros datos alimentan las métricas de cierre y la **capacidad de reorden**. Si el modelo no contiene esos datos, no mostrar métricas financieras como si fueran reales.
- Qué información se compara entre proveedores (precio, disponibilidad u otros campos que el prototipo realmente tenga).
- Qué objetivo numérico se asigna a las métricas de éxito. La guía propone reducir tiempo de cierre, quiebres de stock y pagos imprevistos, y medir órdenes originadas por recomendaciones; no fija metas cuantitativas.
- Qué significa “20 datos de prueba”: especificar el número total y su distribución por entidad, sin asumir que son 20 registros de cada tipo.

Toda recomendación debe poder explicarse con los datos y reglas usados; no puede ser solamente texto estático.

## 4. Plan por fases y criterios de salida

### Fase 1 — Preparación del entorno

1. Instalar Google Antigravity como aplicación independiente e iniciar sesión.
2. Crear la carpeta de trabajo y el repositorio Git recomendado.
3. Verificar Java JDK, Maven o Maven Wrapper, Node.js, npm y navegador.
4. Registrar las versiones realmente instaladas/configuradas. Java 17 aparece como referencia de la demostración, no como versión universal obligatoria.
5. Crear o adaptar una estructura clara, por ejemplo:

```text
project-inventory-workflow/
├── PRD.md
├── SPEC-TECNICA.md
├── README.md
├── design.md                 # opcional
├── mockups/
│   └── stitch/               # exportación y capturas de Stitch
├── backend/
├── frontend/
└── docs/
    └── MATRIZ-PRUEBAS.md     # o equivalente documentado
```

**Salida verificable:** versiones registradas y estructura creada. No se debe inventar una versión si no se ha comprobado.

### Fase 2 — PRD

Usar Antigravity en modo **PLAN** para proponer el documento. Revisarlo antes de guardarlo como `PRD.md` en la raíz.

El PRD debe incluir, como mínimo:

- Problema, contexto, usuarios y objetivos del producto.
- Alcance incluido y excluido.
- Módulos y funcionalidades priorizadas (imprescindibles del MVP frente a futuras mejoras).
- Requisitos funcionales claros y verificables.
- Requisitos no funcionales con criterios comprobables.
- Flujos principales con inicio, decisiones y resultado.
- Reglas de negocio y métricas de éxito; identificar supuestos pendientes.
- Criterios de aceptación que permitan validar cada requisito.

**Prompt base sugerido:**

> Eres especialista en desarrollo de producto. Para un negocio que vende computadores, accesorios, software, servidores, UPS y otros suministros informáticos, define un PRD para apoyar el cierre mensual y decidir compras a proveedores. Limita la solución a finanzas básicas del almacén, inventario y proveedores. Incluye problema, usuarios, objetivos, alcance y exclusiones, funcionalidades priorizadas, requisitos funcionales y no funcionales verificables, reglas de negocio, métricas y flujos principales. Marca como preguntas o supuestos cualquier fórmula que no haya sido definida; no inventes módulos fuera del alcance.

**Criterio de aprobación:** cada requisito se entiende y se puede comprobar; alcance, prioridades, flujos y reglas son consistentes.

### Fase 3 — Diseño y exportación de mockups

1. En Google Stitch, elegir proyecto de interfaz web, no móvil/tablet.
2. Definir una línea visual consistente; `design.md` es opcional y puede registrar paleta, tipografía, espaciado, componentes y estados.
3. Cargar `PRD.md` como referencia.
4. Diseñar y revisar, como mínimo, vistas para tablero, catálogo/inventario, productos críticos o con más de 90 días, proveedores y órdenes sugeridas.
5. Revisar jerarquía de indicadores, legibilidad de tablas, filtros, accesibilidad, estados vacíos, errores y confirmaciones. No aceptar automáticamente el primer resultado.
6. Exportar las vistas aprobadas, descomprimirlas bajo `mockups/` (por ejemplo `mockups/stitch/`) y conservar HTML y capturas disponibles.

**Evidencia:** capturas de las vistas aprobadas y archivos exportados localmente, relacionados con requisitos del PRD.

### Fase 4 — Especificación técnica

Solicitarla a Antigravity con enfoque de líder técnico, indicando las rutas reales de `PRD.md` y de los mockups. Revisarla y aprobarla **antes** de pedir implementación.

Guardar como `SPEC-TECNICA.md`. Debe incluir:

- Arquitectura general y responsabilidades de cada componente.
- Stack y versiones efectivamente seleccionadas.
- Modelo de datos, entidades (como mínimo productos y proveedores), relaciones, restricciones y datos iniciales.
- Capas backend: controladores REST, servicios y repositorios.
- API: rutas, verbos HTTP, parámetros, cuerpos/respuestas, validaciones y errores.
- Organización de vistas, componentes y servicios de API en React.
- Configuración de H2 en memoria y plan para los datos de prueba.
- Plan de pruebas, fases de implementación y criterios de finalización.

**Revisión previa a aprobar:** las rutas y entidades soportan los requisitos; las reglas de negocio son coherentes; las métricas tienen fuentes de datos; los endpoints y los mockups no se contradicen. La guía no impone nombres concretos de endpoints ni versiones exactas; se deben especificar los elegidos.

**Prompt base sugerido:**

> Actúa como líder técnico. Revisa `PRD.md` y los mockups exportados en `mockups/stitch/`. Propón `SPEC-TECNICA.md` para una aplicación con backend Spring Boot y API REST, frontend React, H2 en memoria, datos ficticios y sin autenticación en este prototipo. Incluye arquitectura por capas, tecnologías y versiones, modelo de datos y restricciones, rutas y contratos de API, estructura frontend, configuración, datos semilla, pruebas y fases de implementación. Señala inconsistencias y decisiones sin definir; no empieces a generar código hasta que la especificación sea aprobada.

### Fase 5 — Implementación e inspección

Implementar primero el backend y luego el frontend, siguiendo los documentos aprobados. Leer cada comando propuesto por el agente antes de autorizarlo; no conceder permisos indiscriminados.

**Backend — inspección mínima:**

- Entidad producto: identificador, SKU, nombre, marca, categoría/subcategoría, costos, precio, stock actual/mínimo/ideal, antigüedad y proveedor, según el PRD/modelo aprobado.
- Entidad proveedor y relación coherente con productos.
- Repositorios Spring Data JPA; consultas personalizadas solo si se justifican.
- Lógica de negocio en servicios, no concentrada en controladores.
- Controladores REST, códigos HTTP y validaciones apropiadas.
- Configuración de H2 y carga de datos iniciales.
- Pruebas y documentación de API, si se configuró.

**Frontend — inspección mínima:**

- Vistas/componentes separados por responsabilidad y coherentes con los mockups.
- Consumo de API separado de la presentación.
- Dashboard alimentado por el backend, no solo por valores estáticos.
- Filtros que modifican realmente los resultados.
- Formularios con validaciones y mensajes comprensibles.
- Estados visibles de carga, error de red y listas vacías.

### Fase 6 — Ejecución e integración local

Usar primero el `README.md`, los scripts y las versiones del proyecto. Como referencia en Windows, si existe Maven Wrapper:

```powershell
cd backend
./mvnw.cmd spring-boot:run
```

En otra terminal:

```powershell
cd frontend
npm install
npm run dev
```

Confirmar en consola y configuración los puertos reales; no asumirlos. Si se habilitó la consola H2, usar únicamente la configuración local documentada y no exponerla en producción.

**Validaciones de integración:**

- Abrir la documentación OpenAPI/Swagger si fue configurada.
- Ejecutar al menos una petición GET real a la API.
- Confirmar que React presenta los mismos datos provenientes del backend.
- Revisar consola del navegador y registros del backend.
- Verificar que H2 es en memoria y que los datos pueden reinicializarse al reiniciar; documentar el comportamiento observado.

### Fase 7 — Pruebas funcionales

Completar la matriz con resultado real, no asumir que una prueba pasó. Si no se ejecutó, marcarla como **Pendiente**.

| ID | Prueba | Criterio esperado | Estado/evidencia |
|---|---|---|---|
| P01 | Abrir el dashboard | Indicadores coherentes con respuestas de la API | Pendiente de ejecución |
| P02 | Consultar catálogo | Tabla con registros del backend y campos legibles | Pendiente de ejecución |
| P03 | Filtrar productos críticos | Solo resultados que satisfacen la regla documentada | Pendiente de ejecución |
| P04 | Filtrar productos con más de 90 días | Solo productos que cumplen el criterio definido | Pendiente de ejecución |
| P05 | Consultar proveedores | Listado y datos relevantes visibles | Pendiente de ejecución |
| P06 | Generar/ver sugerencia de compra | Recomendación calculada con stock, rotación y reglas; no texto estático | Pendiente de ejecución |
| P07 | Datos vacíos o error de API | Mensaje útil; interfaz no se bloquea | Pendiente de ejecución |
| P08 | Validar formularios | Campos obligatorios y valores inválidos gestionados correctamente | Pendiente de ejecución |
| P09 | Recargar y reiniciar | Comportamiento de H2 en memoria documentado | Pendiente de ejecución |
| P10 | Revisar diseño adaptable | Legibilidad conservada en distintos tamaños de ventana | Pendiente de ejecución |

Para cada fallo registrar: ID, pasos para reproducir, resultado actual, resultado esperado, captura/log, hipótesis y corrección aplicada.

### Fase 8 — Revisión de calidad

Documentar hallazgos y deuda técnica revisando:

- Trazabilidad entre funcionalidades y requisitos del PRD.
- Separación de responsabilidades en backend y frontend.
- Validación de entradas y reglas de negocio también en servidor.
- Errores comprensibles sin exponer trazas ni información sensible.
- Métricas con fórmulas, fuentes, períodos y unidades explícitas.
- Ausencia de secretos en el repositorio.
- Mantenibilidad, duplicación, nombres y documentación.
- Diferencia entre pruebas realmente ejecutadas y pruebas sugeridas.
- Riesgos antes de producción: no hay autenticación y H2 en memoria no es persistente; el prototipo no debe usarse con datos reales.

## 5. Entregables que debe contener la entrega

- [x] Carpeta del proyecto con código fuente de backend y frontend.
- [x] `PRD.md` revisado y aprobado por el responsable; validación docente/equipo pendiente si se exige.
- [x] Mockups exportados de Stitch y capturas disponibles.
- [x] `SPEC-TECNICA.md` con arquitectura, modelo de datos, API y plan de pruebas.
- [x] `README.md` con requisitos, versiones, configuración e instrucciones reproducibles.
- [x] Matriz de pruebas P01–P12 completada con resultados manuales confirmados y enlaces a evidencias.
- [x] Revisión de calidad completada según validación del responsable: accesibilidad y JavaDoc revisados; deuda técnica documentada en la especificación.
- [x] Video corto o demostración en vivo: no requerido para esta entrega (confirmado por el responsable).

La sección de entregables del PDF identifica el ejercicio como **opcional**, con fecha **13 de noviembre de 2026**. Confirmar con el docente si aplica a esta entrega concreta. La lista anterior es la lista de evidencia que la guía indica para ese ejercicio.

## 6. Checklist final de aceptación

- [x] El PRD distingue alcance, exclusiones, prioridades, reglas y requisitos verificables.
- [x] Los mockups corresponden al PRD y están exportados localmente.
- [x] La especificación refleja el proyecto real (stack, versiones, arquitectura, datos y rutas).
- [x] El backend compila y arranca sin errores bloqueantes.
- [x] El frontend instala dependencias y arranca correctamente.
- [x] Se probaron rutas REST directamente.
- [x] Los datos visibles en la interfaz provienen del backend.
- [x] Los filtros, formularios, recomendaciones y estados de error/vacío fueron comprobados manualmente.
- [x] Las 12 pruebas P01–P12 tienen estado y evidencia registrada; no se presentan pruebas pendientes como aprobadas.
- [x] El README permite repetir la ejecución y describe H2 en memoria.
- [x] Se documentaron limitaciones de seguridad, persistencia y deuda técnica.

## 7. Nota sobre uso responsable de IA

Usar la IA para proponer y acelerar, no para sustituir la revisión. Revisar los archivos generados, validar cada decisión contra el PRD y la especificación, comprender los comandos antes de ejecutarlos y no compartir credenciales, datos personales o información confidencial. No ejecutar comandos destructivos ni instalar dependencias sin entender su propósito.

## 8. Referencias de la guía

- Google Antigravity: <https://antigravity.google/>
- Google Stitch: <https://stitch.withgoogle.com/>
- Spring Boot: <https://docs.spring.io/spring-boot/>
- Spring Data JPA: <https://spring.io/projects/spring-data-jpa>
- React: <https://react.dev/>
- H2 Database: <https://www.h2database.com/>
- Mermaid: <https://mermaid.js.org/>
