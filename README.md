# LogiExpress – API de Gestión de Envíos

Mini-proyecto integrador (IF0009 – Desarrollo de Software IV). API RESTful construida con **Spring Boot**, **JPA**, **Spring Security + JWT** y documentada con **OpenAPI/Swagger**.

- **Java:** 21 (configurado en `pom.xml`)
- **Build:** Maven (wrapper incluido)
- **Base de datos:** SQL Server (producción/desarrollo) · H2 en memoria (solo pruebas)

## 1. Arquitectura y desacoplamiento (Tema 1)

La aplicación sigue un modelo **cliente-servidor desacoplado**: el back-end expone únicamente servicios REST/JSON y no genera HTML. Cualquier cliente (una SPA en React/Angular, una app móvil, otro sistema) consume el mismo contrato HTTP.

```
Cliente (SPA / Móvil)  ──HTTP + JSON + JWT──►  controller  ──►  business  ──►  data  ──►  BD
                                                  (DTOs)      (reglas)     (JPA)
```

| Paquete | Responsabilidad |
|---|---|
| `com.logiexpress.domain` | Entidades del modelo (`Cliente`, `Paquete`, `Usuario`, enums). |
| `com.logiexpress.data` | Repositorios Spring Data JPA y consultas JPQL. |
| `com.logiexpress.business` | Servicios y reglas de negocio (`PaqueteService`, transacciones). |
| `com.logiexpress.controller` | Controladores REST y DTOs (`controller.dto`). |
| `com.logiexpress.config` | Seguridad (JWT, RBAC, BCrypt) y OpenAPI. |
| `com.logiexpress.exception` | Excepciones de negocio y manejador global RFC 7807. |

**Por qué el desacoplamiento importa:**

1. **Independencia de clientes:** el front-end puede evolucionar (o reemplazarse) sin tocar el back-end; solo depende del contrato REST versionado (`/api/v1`).
2. **Reutilización:** la misma API sirve a una web SPA y a una app móvil; la lógica de negocio vive en un solo lugar (`business`).
3. **Capas con responsabilidad única:** el controlador solo traduce HTTP ⇄ DTO; las reglas están en el servicio; la persistencia en los repositorios. Los DTOs evitan exponer las entidades JPA.
4. **Seguridad sin estado:** JWT permite escalar horizontalmente porque el servidor no guarda sesiones.
5. **Testeabilidad:** el servicio se prueba de forma aislada con Mockito, sin BD ni HTTP.

## 2. Configuración

Las credenciales **nunca** se guardan en el repositorio; se leen de variables de entorno:

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_USERNAME` | Usuario de SQL Server | `Lenguajes` |
| `DB_PASSWORD` | Contraseña de SQL Server | *(vacía)* |
| `JWT_SECRET` | Clave de firma JWT (mínimo 32 caracteres) | valor de ejemplo, **cámbielo** |

## 3. Ejecución

```bash
# Linux / macOS
export DB_PASSWORD='su_contraseña'
./mvnw spring-boot:run

# Windows (PowerShell)
$env:DB_PASSWORD = 'su_contraseña'
.\mvnw.cmd spring-boot:run
```

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Al iniciar se crean datos de demostración (si no existen):

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `Admin123!` | `ROLE_ADMIN` |
| `operador` | `Operador123!` | `ROLE_OPERADOR` |

También se crea el cliente demo con id `1` (cédula `1-1111-1111`). Las contraseñas se almacenan con **BCrypt**.

## 4. Endpoints

| Método | Ruta | Roles permitidos |
|---|---|---|
| POST | `/api/v1/auth/login` | Público |
| GET | `/api/v1/paquetes?estado=&page=&size=` | ADMIN, OPERADOR |
| GET | `/api/v1/paquetes/{id}` | ADMIN, OPERADOR |
| POST | `/api/v1/paquetes` | ADMIN |
| DELETE | `/api/v1/paquetes/{id}` | ADMIN |

### Ejemplo de uso

```bash
# 1) Obtener token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123!"}'

# 2) Registrar un paquete
curl -X POST http://localhost:8080/api/v1/paquetes \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d '{"clienteId":1,"codigoRastreo":"LX-2026-0001","descripcion":"Caja con documentos","pesoKg":12.5}'
```

## 5. Manejo de errores (RFC 7807)

Todos los errores devuelven `application/problem+json` con `type`, `title`, `status`, `detail`, `instance` y `timestamp`:

```json
{
  "type": "https://logiexpress.com/errors/peso-excedido",
  "title": "Peso excedido",
  "status": 400,
  "detail": "El peso del paquete (35.0 kg) supera el máximo permitido de 30.0 kg.",
  "instance": "/api/v1/paquetes",
  "timestamp": "2026-09-28T15:30:00Z"
}
```

| Excepción | HTTP |
|---|---|
| `PesoExcedidoException` | 400 |
| `ClienteNoEncontradoException` / `PaqueteNoEncontradoException` | 404 |
| `CodigoRastreoDuplicadoException` | 409 |
| Validación `@Valid` | 400 (con detalle por campo en `errors`) |
| Sin token / token inválido | 401 |
| Rol sin permiso | 403 |

## 6. Pruebas

```bash
./mvnw test
```

`PaqueteServiceTest` (JUnit 5 + Mockito) verifica el registro exitoso, el rechazo por peso > 30 kg (`assertThrows`) y cliente inexistente. Las pruebas de contexto usan H2 en memoria.

## 7. Flujo de Git

- Rama principal: `main`.
- Commits siguiendo *Conventional Commits* (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`).
- `feature/security` y `feature/testing` se integraron a `main` mediante `merge --no-ff`.
