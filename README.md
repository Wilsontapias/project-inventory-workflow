# Inventario y flujo de caja

Prototipo web de gestión de inventario, proveedores y sugerencias de reabastecimiento. Conserva los archivos exportados de Stitch como referencia visual; la aplicación React replica sus patrones de navegación, tarjetas, tablas, tipografía y estados con CSS local.

## Integrantes

- Wilson Tapias Posada
- Juan Jose Montoya Hernandez
- Sofia Ospina Gomez
- Juan Sebastian Zapata Munera
- Santiago Patiño Gomez

## Estructura

```text
backend/                       # Java 17, Spring Boot 4.0.9, REST, JPA, H2
frontend/                      # React 19 y Vite 8
stitch_inventario_y_flujo_de_caja/  # Mockups originales exportados
GUIA_EJECUCION_LABORATORIO.md  # Requisitos y checklist del laboratorio
PRD.md                         # Requisitos del producto (aprobado por el responsable)
SPEC-TECNICA.md                # Arquitectura, modelo, API y pruebas
docs/MATRIZ-PRUEBAS.md         # Estado y evidencias de pruebas
```

## Ejecutar localmente en Windows

1. Iniciar el backend en una terminal:

   ```powershell
   cd backend
   ./mvnw.cmd spring-boot:run
   ```

2. Iniciar el frontend en otra terminal:

   ```powershell
   cd frontend
   npm.cmd install
   npm.cmd run dev
   ```

3. Abrir <http://127.0.0.1:5173>. La URL de la API se configura con `VITE_API_BASE_URL` en `frontend/.env.local`; por defecto es `http://localhost:8080/api`.

Para verificar cada parte:

```powershell
cd backend
./mvnw.cmd test
./mvnw.cmd javadoc:javadoc
```

```powershell
cd frontend
npm.cmd run lint
npm.cmd run build
```

## Limitaciones del prototipo

- H2 es una base en memoria: reiniciar el backend vuelve a cargar datos ficticios.
- No hay autenticación; la consola H2 y los datos son solo para desarrollo local.
- El flujo de caja, saldo inicial COP 320 M, movimientos y compromisos son demostrativos; no representan el saldo real del negocio.
- No hay historial completo de ventas/rotación, compras reales ni órdenes aprobadas.
- No utilizar datos personales, bancarios, comerciales reales ni desplegar esta configuración como producción.

Consulta `backend/README.md` y `frontend/README.md` para la estructura, reglas de negocio y contratos REST.
