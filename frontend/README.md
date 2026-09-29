# LogiExpress – Front-End (Angular 19)

SPA que consume la API REST de LogiExpress (`/api/v1`).

**Características:** componentes standalone, Signals (`signal`, `computed`), control de flujo (`@if`, `@for`, `@switch`), interceptor JWT funcional (`HttpInterceptorFn`), guards funcionales (`CanActivateFn`), lazy loading (`loadComponent`) y pruebas con `TestBed`.

## Requisitos
- Node.js 20+ y npm
- Back-end en ejecución en `http://localhost:8080` (ver README de la raíz)

## Comandos
```bash
npm install
npm start        # http://localhost:4200
npm test         # pruebas unitarias (Karma + Jasmine, requiere Chrome)
npm run build    # compilación de producción en dist/
```

La URL de la API se configura en `src/app/config/api.config.ts`.

## Usuarios de demostración
| Usuario | Contraseña | Puede |
|---|---|---|
| `admin` | `Admin123!` | Consultar, registrar y eliminar paquetes |
| `operador` | `Operador123!` | Solo consultar paquetes |
