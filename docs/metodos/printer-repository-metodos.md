# Documentación de Métodos - PrinterRepositoryImpl

Este documento describe detalladamente todos los métodos de la clase `PrinterRepositoryImpl`, que implementa la capa de acceso a datos (Data Access Layer) para las impresoras en el sistema.

## Índice

1. [Descripción General](#descripción-general)
2. [Constructor](#constructor)
3. [Inicialización](#inicialización)
4. [Métodos CRUD](#métodos-crud)
5. [Métodos Auxiliares](#métodos-auxiliares)
6. [Mapeo de Datos](#mapeo-de-datos)

---

## Descripción General

### Clase: `PrinterRepositoryImpl`

**Propósito:** Implementación de la interfaz `PrinterRepository` que proporciona operaciones de lectura y escritura en la tabla `MASTER_PRINTER` de la base de datos SQL Server.

**Patrón Utilizados:**
- **Repository Pattern**: Abstrae la lógica de acceso a datos
- **Spring JDBC**: Utiliza `JdbcTemplate` y `SimpleJdbcCall` para ejecutar queries y stored procedures
- **DTO Pattern**: Usa `PrinterDTO` para transferir datos entre capas

## Constructor

### `PrinterRepositoryImpl(JdbcTemplate jdbcTemplate, DataSource dataSource)`

**Propósito:** Inicializa la instancia del repositorio con las dependencias inyectadas y configura los stored procedures.

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `jdbcTemplate` | `JdbcTemplate` | Template de Spring JDBC para ejecutar queries SQL directas |
| `dataSource` | `DataSource` | Fuente de datos para crear conexiones y ejecutar stored procedures |

**Flujo:**
```
1. Asigna jdbcTemplate y dataSource a variables miembro
2. Invoca initializeSimpleJdbcCalls() para configurar los stored procedures
3. Queda listo para ejecutar operaciones CRUD
```

**Ejemplo de Inyección:**
```java
@Autowired
private PrinterRepositoryImpl printerRepository;
```

**Nota:** La inyección de dependencias es automática en Spring Boot a través del constructor anotado con `@Repository`.

---

## Inicialización

### `initializeSimpleJdbcCalls()`

**Propósito:** Configura las instancias de `SimpleJdbcCall` para cada stored procedure utilizado en las operaciones CRUD. Este método es crítico porque declara explícitamente los parámetros que cada SP espera.

**Acceso:** Privado (solo accesible internamente)

**Llamado por:** Constructor, en el inicializador de la clase

### Stored Procedures Configurados

#### 1. **MASTER_INPRINT** (Crear Impresora)

```java
this.crearImpresoraCall = new SimpleJdbcCall(dataSource)
    .withProcedureName("MASTER_INPRINT")
    .withoutProcedureColumnMetaDataAccess()
    .declareParameters(
        new SqlParameter("PRI002", Types.VARCHAR),    // Nombre
        new SqlParameter("PRI003", Types.VARCHAR),    // IP
        new SqlParameter("PRI004", Types.VARCHAR),    // Ubicación
        new SqlParameter("PRI005", Types.VARCHAR),    // Tipo
        new SqlParameter("PRI006", Types.VARCHAR),    // Descripción
        new SqlParameter("PRI007", Types.TIMESTAMP),  // Fecha Registro
        new SqlParameter("PRI008", Types.INTEGER));   // Estado
```

**Parámetros:**
| Código | Nombre | Tipo | Descripción |
|--------|--------|------|-------------|
| PRI002 | nombre | VARCHAR | Nombre de la impresora |
| PRI003 | ip | VARCHAR | Dirección IP de la impresora |
| PRI004 | ubicacion | VARCHAR | Ubicación física |
| PRI005 | tipo | VARCHAR | Tipo/modelo de impresora |
| PRI006 | descripcion | VARCHAR | Descripción adicional |
| PRI007 | fechaRegistro | TIMESTAMP | Fecha/hora de registro (auto-generada) |
| PRI008 | estado | INTEGER | Estado inicial (1=activo) |

**Retorno:** ID generado (SCOPE_IDENTITY()) de la impresora creada

---

#### 2. **MASTER_UPRINT** (Actualizar Impresora)

```java
this.actualizarImpresoraCall = new SimpleJdbcCall(dataSource)
    .withProcedureName("MASTER_UPRINT")
    .withoutProcedureColumnMetaDataAccess()
    .declareParameters(
        new SqlParameter("PRI001", Types.INTEGER),    // ID
        new SqlParameter("PRI002", Types.VARCHAR),    // Nombre
        new SqlParameter("PRI003", Types.VARCHAR),    // IP
        new SqlParameter("PRI004", Types.VARCHAR),    // Ubicación
        new SqlParameter("PRI005", Types.VARCHAR),    // Tipo
        new SqlParameter("PRI006", Types.VARCHAR),    // Descripción
        new SqlParameter("PRI008", Types.INTEGER));   // Estado
```

**Parámetros:**
| Código | Nombre | Tipo | Descripción |
|--------|--------|------|-------------|
| PRI001 | id | INTEGER | ID de la impresora a actualizar |
| PRI002-PRI006 | campos | VARCHAR | Campos actualizables |
| PRI008 | estado | INTEGER | Nuevo estado |

**Retorno:** ResultSet con el registro actualizado (SELECT completo)

---

#### 3. **MASTER_DPRINT** (Eliminar Lógico)

```java
this.eliminarImpresoraCall = new SimpleJdbcCall(dataSource)
    .withProcedureName("MASTER_DPRINT")
    .withoutProcedureColumnMetaDataAccess()
    .declareParameters(
        new SqlParameter("PRI001", Types.INTEGER));   // ID
```

**Parámetros:**
| Código | Nombre | Tipo | Descripción |
|--------|--------|------|-------------|
| PRI001 | id | INTEGER | ID de la impresora a desactivar |

**Función:** Soft delete - Marca el registro como inactivo sin eliminarlo físicamente

**Retorno:** Sin retorno (void)

---

#### 4. **MASTER_DDPRINT** (Eliminar Definido)

```java
this.eliminarImpresoraDefinitivoCall = new SimpleJdbcCall(dataSource)
    .withProcedureName("MASTER_DDPRINT")
    .withoutProcedureColumnMetaDataAccess()
    .declareParameters(
        new SqlParameter("PRI001", Types.INTEGER));   // ID
```

**Parámetros:**
| Código | Nombre | Tipo | Descripción |
|--------|--------|------|-------------|
| PRI001 | id | INTEGER | ID de la impresora a eliminar |

**Función:** Hard delete - Elimina el registro permanentemente de la base de datos

**Retorno:** Sin retorno (void)

---

### Concepto Importante: `withoutProcedureColumnMetaDataAccess()`

Cuando usamos `.withoutProcedureColumnMetaDataAccess()`, le indicamos a Spring JDBC que **no intente descubrir automáticamente** los parámetros del SP. Por esto, debemos declarar explícitamente todos los parámetros usando `.declareParameters()`.

Sin esta declaración explícita, Spring lanzará error: `"Procedure or function 'MASTER_XXXX' expects parameter '@PRIXX', which was not supplied."`

---

## Métodos CRUD

### LECTURA (READ)

#### `obtenerImpresorasActivas()`

**Propósito:** Obtiene todas las impresoras que están activas en el sistema.

**Firma:**
```java
@Override
public List<PrinterDTO> obtenerImpresorasActivas()
```

**Parámetros:** Ninguno

**Retorno:** `List<PrinterDTO>` con todas las impresoras activas, o lista vacía si hay error

**Stored Procedure:** `MASTER_GdataActivePRINT`

**Flujo:**
```
1. Ejecuta query: EXEC MASTER_GdataActivePRINT
2. Itera sobre cada fila del ResultSet
3. Mapea cada fila a PrinterDTO usando mapPrinterDTO()
4. Retorna lista completa o lista vacía si hay excepción
```

**Manejo de Errores:**
- Captura excepciones genéricas
- Registra error en logs
- Retorna lista vacía (no falla la aplicación)

**Ejemplo de Uso:**
```java
List<PrinterDTO> impresoras = printerRepository.obtenerImpresorasActivas();
if (!impresoras.isEmpty()) {
    impresoras.forEach(p -> System.out.println(p.getNombre()));
}
```

---

#### `obtenerImpresoraPorId(int id)`

**Propósito:** Obtiene una impresora específica por su ID, sin importar su estado.

**Firma:**
```java
@Override
public PrinterDTO obtenerImpresoraPorId(int id)
```

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | `int` | ID único de la impresora a buscar |

**Retorno:** `PrinterDTO` si la impresora existe, `null` si no existe o hay error

**Stored Procedure:** `MASTER_GdataPRINTbyId`

**Flujo:**
```
1. Ejecuta query con parámetro: EXEC MASTER_GdataPRINTbyId ?
2. Pasa el ID como parámetro posicional
3. Convierte el resultado en List<PrinterDTO>
4. Retorna el primer elemento o null si la lista está vacía
5. Si hay excepción, registra en DEBUG y retorna null
```

**Nota:** Usa `@SuppressWarnings("deprecation")` porque el método de parámetros posicionales está deprecado en Spring 6+, pero aún funciona.

**Ejemplo de Uso:**
```java
PrinterDTO impresora = printerRepository.obtenerImpresoraPorId(5);
if (impresora != null) {
    System.out.println("Impresora encontrada: " + impresora.getNombre());
} else {
    System.out.println("Impresora no encontrada");
}
```

---

### CREACIÓN (CREATE)

#### `crearImpresora(PrinterDTO printerDTO)`

**Propósito:** Inserta una nueva impresora en la base de datos.

**Firma:**
```java
@Override
public PrinterDTO crearImpresora(PrinterDTO printerDTO)
```

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `printerDTO` | `PrinterDTO` | Objeto con datos de la impresora a crear |

**Retorno:** `PrinterDTO` con la impresora completa (incluye ID generado y fecha), o `null` si hay error

**Stored Procedure:** `MASTER_INPRINT`

**Flujo Detallado:**

```
1. PREPARACIÓN DE PARÁMETROS
   ├─ Creamos MapSqlParameterSource con parámetros nombrados
   ├─ PRI002: printerDTO.getNombre()
   ├─ PRI003: printerDTO.getIp()
   ├─ PRI004: printerDTO.getUbicacion()
   ├─ PRI005: printerDTO.getTipo()
   ├─ PRI006: printerDTO.getDescripcion()
   ├─ PRI007: LocalDateTime.now() (fecha actual del servidor)
   └─ PRI008: 1 (estado activo por defecto)

2. EJECUCIÓN DEL SP
   ├─ Ejecuta MASTER_INPRINT con los parámetros
   └─ SP retorna SCOPE_IDENTITY() (ID generado)

3. PROCESAMIENTO DEL RESULTADO
   ├─ Extrae el ID del Map de resultados
   ├─ Convierte a Integer
   └─ Invoca obtenerImpresoraPorId() con ese ID

4. RETORNO
   ├─ Si éxito: PrinterDTO completo con todos los campos
   ├─ Si fail: null o excepción
   └─ Si DataAccessException: lanza RuntimeException
```

**Parámetros del SP:**

| Código | Valor | Tipo | Origen |
|--------|-------|------|--------|
| PRI002 | nombre | VARCHAR | printerDTO.getNombre() |
| PRI003 | IP | VARCHAR | printerDTO.getIp() |
| PRI004 | ubicación | VARCHAR | printerDTO.getUbicacion() |
| PRI005 | tipo | VARCHAR | printerDTO.getTipo() |
| PRI006 | descripción | VARCHAR | printerDTO.getDescripcion() |
| PRI007 | fecha actual | TIMESTAMP | LocalDateTime.now() |
| PRI008 | 1 | INTEGER | Estado = Activo |

**Manejo de Errores:**
- `DataAccessException`: Lanza `RuntimeException` propagando el error al controlador
- El controlador debe manejar la excepción

**Ejemplo de Uso:**
```java
PrinterDTO nuevaImpresora = new PrinterDTO();
nuevaImpresora.setNombre("Canon IR2520");
nuevaImpresora.setIp("192.168.1.100");
nuevaImpresora.setUbicacion("Oficina A");
nuevaImpresora.setTipo("Multifuncional");
nuevaImpresora.setDescripcion("Impresora para el área de ventas");

PrinterDTO resultado = printerRepository.crearImpresora(nuevaImpresora);
if (resultado != null) {
    System.out.println("Creada con ID: " + resultado.getId());
}
```

---

### ACTUALIZACIÓN (UPDATE)

#### `actualizarImpresora(PrinterDTO printerDTO)`

**Propósito:** Actualiza los datos de una impresora existente.

**Firma:**
```java
@Override
public PrinterDTO actualizarImpresora(PrinterDTO printerDTO)
```

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `printerDTO` | `PrinterDTO` | Objeto con ID y nuevos datos |

**Retorno:** `PrinterDTO` actualizado con todos los campos, o `null` si hay error

**Stored Procedure:** `MASTER_UPRINT`

**Característica Importante:** 
El SP retorna directamente el registro actualizado mediante una sentencia SELECT. Este método **NO hace una query separada** después de actualizar, lo que previene:
- Duplicación de registros
- Race conditions
- Llamadas innecesarias a la BD

**Flujo Detallado:**

```
1. PREPARACIÓN DE PARÁMETROS
   ├─ Creamos MapSqlParameterSource con parámetros nombrados
   ├─ PRI001: printerDTO.getId() (identificador del registro)
   ├─ PRI002-PRI006: nuevos valores de campos
   └─ PRI008: printerDTO.getEstado() (nuevo estado)

2. EJECUCIÓN DEL SP
   ├─ Ejecuta MASTER_UPRINT con los parámetros
   ├─ SP actualiza el registro en DB
   └─ SP retorna SELECT con el registro actualizado

3. PROCESAMIENTO DEL RESULTSET
   ├─ Extrae el Map del resultado
   ├─ Itera buscando un objeto ResultSet
   ├─ Llama rs.next() para obtener la primera fila
   └─ Mapea el resultado a PrinterDTO

4. RETORNO
   ├─ Si éxito: PrinterDTO con datos actualizados
   ├─ Si error: null o excepción
   └─ Excepciones: DataAccessException o SQLException
```

**Parámetros del SP:**

| Código | Significado | Tipo | Fuente |
|--------|-------------|------|--------|
| PRI001 | ID a actualizar | INTEGER | printerDTO.getId() |
| PRI002 | Nuevo nombre | VARCHAR | printerDTO.getNombre() |
| PRI003 | Nueva IP | VARCHAR | printerDTO.getIp() |
| PRI004 | Nueva ubicación | VARCHAR | printerDTO.getUbicacion() |
| PRI005 | Nuevo tipo | VARCHAR | printerDTO.getTipo() |
| PRI006 | Nueva descripción | VARCHAR | printerDTO.getDescripcion() |
| PRI008 | Nuevo estado | INTEGER | printerDTO.getEstado() |

**Manejo de Errores:**
- `DataAccessException`: Error en la BD → RuntimeException
- `SQLException`: Error procesando ResultSet → RuntimeException
- Ambos registran el error y lanzan excepción

**Ventajas de este Enfoque:**

**Atomicidad:** Todo ocurre en una sola transacción  
**Consistencia:** El dato retornado es exactamente lo que se guardó  
**Eficiencia:** Solo 1 llamada a BD en lugar de 2  
**Seguridad:** Previene duplicados y race conditions  

**Ejemplo de Uso:**
```java
PrinterDTO impresora = new PrinterDTO();
impresora.setId(5);
impresora.setNombre("Canon IR2520 (Actualizada)");
impresora.setIp("192.168.1.101");
impresora.setUbicacion("Oficina B");
impresora.setTipo("Multifuncional");
impresora.setDescripcion("Reubicada a oficina B");
impresora.setEstado(1);

PrinterDTO actualizada = printerRepository.actualizarImpresora(impresora);
if (actualizada != null) {
    System.out.println("Actualizada: " + actualizada.getNombre());
}
```

---

### ELIMINACIÓN (DELETE)

#### `eliminarImpresora(int id)` - Soft Delete

**Propósito:** Desactiva lógicamente una impresora (cambio de estado). Los datos no se pierden.

**Firma:**
```java
@Override
public void eliminarImpresora(int id)
```

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | `int` | ID de la impresora a desactivar |

**Retorno:** `void` (sin retorno)

**Stored Procedure:** `MASTER_DPRINT`

**Flujo:**
```
1. Crea MapSqlParameterSource con ID
2. Ejecuta MASTER_DPRINT
3. SP marca el registro como inactivo (generalmente cambia estado a 0)
4. No hay ProcessResult, es una operación fire-and-forget
```

**Diferencia con Hard Delete:**
- **Soft Delete (DPRINT):** Marca como inactivo, datos permanecen en BD
- **Hard Delete (DDPRINT):** Elimina completamente, no recuperable

**Uso Recomendado:**
El soft delete es preferible porque:
- Permite auditoría y historial
- Datos no se pierden si hay error
- Fácil de revertir
- GDPR compliance (mantiene registros)

**Ejemplo de Uso:**
```java
try {
    printerRepository.eliminarImpresora(5);
    System.out.println("Impresora desactivada");
} catch (Exception e) {
    System.out.println("Error al desactivar: " + e.getMessage());
}
```

---

#### `eliminarImpresoraDefinitivo(int id)` - Hard Delete

**Propósito:** Elimina permanentemente una impresora de la base de datos.

**Firma:**
```java
@Override
public void eliminarImpresoraDefinitivo(int id)
```

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | `int` | ID de la impresora a eliminar permanentemente |

**Retorno:** `void` (sin retorno)

**Stored Procedure:** `MASTER_DDPRINT`

**Flujo:**
```
1. Crea MapSqlParameterSource con ID
2. Ejecuta MASTER_DDPRINT
3. SP elimina el registro físicamente de la BD
4. La operación es irreversible
```

**⚠️ Advertencia:** Esta operación es permanente. Se recomienda:
- Usar solo en casos especiales
- Implementar auditoría antes
- Solicitar confirmación del usuario
- Hacer backup de datos

**Ejemplo de Uso:**
```java
// Usar solo después de confirmación del usuario
if (usuarioConfirma) {
    try {
        printerRepository.eliminarImpresoraDefinitivo(5);
        System.out.println("Eliminada definitivamente");
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
```

---

## Métodos Auxiliares

### `mapPrinterDTO(ResultSet rs)` - Mapeo de Datos

**Propósito:** Convierte una fila del ResultSet en un objeto `PrinterDTO`.

**Firma:**
```java
private PrinterDTO mapPrinterDTO(ResultSet rs) throws SQLException
```

**Acceso:** Privado (solo uso interno)

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `rs` | `ResultSet` | Fila actual del resultado de la BD |

**Retorno:** `PrinterDTO` con todos los campos mapeados

**Excepciones:** `SQLException` si hay error al leer del ResultSet

**Mapeo de Columnas:**

| Código | Columna BD | Método DTO | Tipo | Descripción |
|--------|-----------|-----------|------|-------------|
| PRI001 | ID | setId() | int | Identificador único |
| PRI002 | NOMBRE | setNombre() | String | Nombre de la impresora |
| PRI003 | IP | setIp() | String | Dirección IP |
| PRI004 | UBICACION | setUbicacion() | String | Ubicación física |
| PRI005 | TIPO | setTipo() | String | Tipo/modelo |
| PRI006 | DESCRIPCION | setDescripcion() | String | Descripción |
| PRI007 | FECHA_REGISTRO | setFechaRegistro() | LocalDateTime | Fecha de creación |
| PRI008 | ESTADO | setEstado() | int | Estado (1=activo, 0=inactivo) |

**Flujo:**
```
1. Crea una nueva instancia de PrinterDTO
2. Lee cada columna del ResultSet usando rs.getXXX()
3. Asigna el valor al campo correspondiente del DTO
4. Para TIMESTAMP, convierte a LocalDateTime
5. Retorna el DTO completamente poblado
```

**Conversión de Tipos:**
```
TIMESTAMP(PRI007) → java.sql.Timestamp → LocalDateTime
  (mediante .toLocalDateTime())
```

**Ejemplo Interno (Usado por RowMapper):**
```java
// En obtenerImpresorasActivas():
(rs, rowNum) -> mapPrinterDTO(rs)
// Esta lambda llama mapPrinterDTO para cada fila
```

---

## Patrones de Comunicación

### Arquitectura General

```
┌─────────────────────────────────────────────────────┐
│              CONTROLADOR HTTP                       │
│  (PrinterController.java)                           │
└────────────────────┬────────────────────────────────┘
                     │ Solicitud/Respuesta
                     ▼
┌─────────────────────────────────────────────────────┐
│              SERVICIO                               │
│  (PrinterServiceImpl.java)                          │
└────────────────────┬────────────────────────────────┘
                     │ CRUD Operations
                     ▼
┌─────────────────────────────────────────────────────┐
│         REPOSITORIO (Esta Clase)                    │
│  (PrinterRepositoryImpl.java)                       │
└────────────────────┬────────────────────────────────┘
                     │ SQL / Stored Procedures
                     ▼
┌─────────────────────────────────────────────────────┐
│        BASE DE DATOS - SQL SERVER                   │
│  MASTER_PRINTER (tabla)                             │
│  MASTER_INPRINT, MASTER_UPRINT, etc. (SPs)          │
└─────────────────────────────────────────────────────┘
```

### Parámetros Nombrados con MapSqlParameterSource

Todos nuestros métodos de mutación (CREATE, UPDATE, DELETE) usan **parámetros nombrados** en lugar de posicionales:

```java
// ✅ Parámetro Nombrado (Recomendado)
MapSqlParameterSource params = new MapSqlParameterSource()
    .addValue("PRI002", "Canon");
call.execute(params);

// ❌ Parámetro Posicional (Deprecado)
call.execute(new Object[] { "Canon" });
```

**Ventajas:**
- Más legible
- Menos propenso a errores
- Compatible con cualquier orden de parámetros
- Funciona mejor en SQL Server

---

## Tabla de Resumen de Métodos

| Método | Tipo | SP | Parámetros | Retorno | Proposito |
|--------|------|----|----|---------|----------|
| `obtenerImpresorasActivas()` | GET | GdataActivePRINT | - | List<DTO> | Listar activas |
| `obtenerImpresoraPorId()` | GET | GdataPRINTbyId | id | DTO\|null | Buscar por ID |
| `crearImpresora()` | CREATE | MASTER_INPRINT | DTO | DTO | Crear nueva |
| `actualizarImpresora()` | UPDATE | MASTER_UPRINT | DTO | DTO | Actualizar |
| `eliminarImpresora()` | DELETE | MASTER_DPRINT | id | void | Soft delete |
| `eliminarImpresoraDefinitivo()` | DELETE | MASTER_DDPRINT | id | void | Hard delete |
| `mapPrinterDTO()` | Helper | - | RS | DTO | Convertir fila |

---

## Notas sobre Manejo de Errores

### Logging

Todos los métodos utilizan un `Logger` para registrar eventos:

```java
private static final Logger logger = LoggerFactory.getLogger(PrinterRepositoryImpl.class);
```

**Niveles Utilizados:**
- `ERROR`: Fallos en CREATE, UPDATE (propagables)
- `DEBUG`: Info de GET cuando no encuentra registro
- `ERROR`: Excepciones en resultSet de UPDATE

### Estrategia de Excepciones

```
┌─────────────────────────────────┐
│    DataAccessException          │
│    (Error en BD o SP)           │
└──────────────┬──────────────────┘
               │
      ┌────────┴────────┐
      │                 │
   GET              CREATE/UPDATE
   │                │
   └─► Registra    └─► Lanza
       Retorna null   RuntimeException
```

---

## Optimizaciones Realizadas

### Eliminación de Queries Redundantes

**Antes:**
```java
// Hacía 2 queries: INSERT + SELECT
crearImpresoraCall.execute();
return obtenerImpresoraPorId(idGenerado); // 2da query
```

**Después:**
```java
// El SP retorna el SELECT directamente
Map<String, Object> result = actualizarImpresoraCall.execute();
// Procesa el ResultSet del SP
```

**Beneficio:** 50% menos llamadas a BD, previene race conditions

---

Documento del 10 de febrero de 2026  
Atte: opdatos
---