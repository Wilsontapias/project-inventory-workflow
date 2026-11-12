# Frontend — Nexus Inventory

Aplicación React 19 con Vite 8 para consumir la API REST de Spring Boot. Los componentes traducen el lenguaje visual de los mockups de Stitch a CSS local: navegación empresarial, Inter/Roboto Mono, tarjetas KPI, tablas compactas y estados semánticos. No depende del Tailwind CDN exportado por Stitch.

## Requisitos y ejecución

- Node.js 20.19+ (el entorno usado para este prototipo tiene Node 24).
- npm 11 en el entorno de referencia.
- Backend Spring Boot ejecutándose en `http://localhost:8080`.

En PowerShell, desde esta carpeta:

```powershell
npm.cmd install
npm.cmd run dev
```

Vite escucha en `http://127.0.0.1:5173`. Para generar la versión de producción y verificar el empaquetado:

```powershell
npm.cmd run build
```

## API y configuración

La URL por defecto es `http://localhost:8080/api`. Para cambiarla, copia `.env.example` como `.env.local` y modifica `VITE_API_BASE_URL`. Las variables con prefijo `VITE_` quedan expuestas en el código del navegador; no colocar secretos allí.

La app usa estos contratos del backend:

- `GET /dashboard/summary`
- `GET /products` con filtros `search`, `category`, `status`, `olderThanDays`, `lowRotation`
- `POST`, `PUT` y `DELETE /products`
- `GET /suppliers`; `POST`, `PUT` y `DELETE /suppliers`
- `GET /purchase-suggestions`
- `GET /products/{id}/supplier-offers`
- `GET /cash-flow/summary`, `GET/POST /cash-flow/movements` y `GET /cash-flow/movements/{id}`
- `GET/POST /cash-flow/commitments`, `GET /cash-flow/commitments/{id}` y `PATCH /cash-flow/commitments/{id}/pay`

## Estructura

```text
src/
├── app/                       # Aplicación y estado de navegación
├── components/common/         # Iconos, tablas/estados y elementos comunes
├── components/layout/         # Sidebar, topbar y notificaciones
├── features/
│   ├── dashboard/
│   ├── products/
│   ├── suppliers/
│   ├── purchases/
│   └── cashflow/
├── services/                  # Cliente HTTP y servicios por recurso
├── utils/                     # Formato COP y etiquetas
├── App.css                    # Componentes visuales y responsive
└── index.css                  # Tokens visuales y estilos base
```

Los archivos originales exportados por Stitch permanecen en la raíz como referencia; los estilos de ejecución están en este frontend y usan tokens CSS locales.

## Alcance visible

- Dashboard derivado del backend con movimientos y compromisos explícitamente ficticios; no representa la caja real del negocio.
- Catálogo con búsqueda/filtros y altas, edición y eliminación de producto.
- Alertas de stock crítico, antigüedad estrictamente mayor a 90 días y baja rotación con la regla aprobada.
- Directorio con alta y edición de proveedores, condiciones comerciales ficticias y búsqueda.
- Sugerencias calculadas por el backend y diálogo de comparación de ofertas. El frontend no registra ni aprueba órdenes reales.
- Flujo de caja demostrativo con ingreso/gasto, compromisos y acción de pago.
