/* =====================================================================
   LogiExpress - Script de base de datos (SQL Server)
   Base de datos: ExpresoFastC5H489
   Los nombres de tablas/columnas coinciden con las entidades JPA.
   ===================================================================== */

-- 1) Crear la base de datos (solo si no existe)
IF DB_ID(N'ExpresoFastC5H489') IS NULL
BEGIN
    CREATE DATABASE ExpresoFastC5H489;
END
GO

USE ExpresoFastC5H489;
GO

-- 2) Tablas
IF OBJECT_ID(N'dbo.clientes', N'U') IS NULL
CREATE TABLE dbo.clientes (
    id      BIGINT IDENTITY(1,1) NOT NULL,
    cedula  VARCHAR(20)  NOT NULL,
    nombre  VARCHAR(150) NOT NULL,
    correo  VARCHAR(150) NOT NULL,
    CONSTRAINT PK_clientes PRIMARY KEY (id),
    CONSTRAINT UQ_clientes_cedula UNIQUE (cedula)
);
GO

IF OBJECT_ID(N'dbo.paquetes', N'U') IS NULL
CREATE TABLE dbo.paquetes (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    codigo_rastreo VARCHAR(50)  NOT NULL,
    descripcion    VARCHAR(255) NOT NULL,
    peso_kg        FLOAT        NOT NULL,
    estado         VARCHAR(20)  NOT NULL,
    cliente_id     BIGINT       NOT NULL,
    CONSTRAINT PK_paquetes PRIMARY KEY (id),
    CONSTRAINT UQ_paquetes_codigo UNIQUE (codigo_rastreo),
    CONSTRAINT FK_paquetes_cliente FOREIGN KEY (cliente_id) REFERENCES dbo.clientes (id),
    CONSTRAINT CK_paquetes_estado CHECK (estado IN ('REGISTRADO', 'EN_TRANSITO', 'ENTREGADO')),
    CONSTRAINT CK_paquetes_peso   CHECK (peso_kg > 0 AND peso_kg <= 30.0)
);
GO

IF OBJECT_ID(N'dbo.usuarios', N'U') IS NULL
CREATE TABLE dbo.usuarios (
    id       BIGINT IDENTITY(1,1) NOT NULL,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,   -- hash BCrypt
    rol      VARCHAR(20)  NOT NULL,
    CONSTRAINT PK_usuarios PRIMARY KEY (id),
    CONSTRAINT UQ_usuarios_username UNIQUE (username),
    CONSTRAINT CK_usuarios_rol CHECK (rol IN ('ROLE_ADMIN', 'ROLE_OPERADOR'))
);
GO

-- 3) Datos iniciales (contraseñas con BCrypt: admin = Admin123!, operador = Operador123!)
IF NOT EXISTS (SELECT 1 FROM dbo.usuarios WHERE username = 'admin')
    INSERT INTO dbo.usuarios (username, password, rol)
    VALUES ('admin', '$2a$10$oo92vyll9WfbUfCKwihLHe1dAn3XgFzViHWBNPP03IIU82cPFGCa2', 'ROLE_ADMIN');

IF NOT EXISTS (SELECT 1 FROM dbo.usuarios WHERE username = 'operador')
    INSERT INTO dbo.usuarios (username, password, rol)
    VALUES ('operador', '$2a$10$C8RjF6mpzCBd1EPWX2r/U.gXnar4F60.opdPuT1O3xQ0NbW1Yjubm', 'ROLE_OPERADOR');

IF NOT EXISTS (SELECT 1 FROM dbo.clientes WHERE cedula = '1-1111-1111')
    INSERT INTO dbo.clientes (cedula, nombre, correo)
    VALUES ('1-1111-1111', 'Cliente Demo', 'demo@logiexpress.com');
GO

-- 4) Verificación
SELECT 'clientes' AS tabla, COUNT(*) AS filas FROM dbo.clientes
UNION ALL SELECT 'paquetes', COUNT(*) FROM dbo.paquetes
UNION ALL SELECT 'usuarios', COUNT(*) FROM dbo.usuarios;
GO
