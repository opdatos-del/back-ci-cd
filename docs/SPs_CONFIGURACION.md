# 📋 Guía: Configuración Centralizada de Stored Procedures (SPs)

## 🤔 ¿Cuál es el Problema que Resolvemos?

### ANTES (Sin patrón centralizado)
```java
// EmpleadoRepositoryImpl.java
public List<Empleado> obtenerEmpleados() {
    // Hardcoding: Base de datos y SP quemados en el código
    String sql = "EXEC [JovyDataADW_DEV].[dbo].[MASTER_GPDK_1_DEV] @param1 = ?";
    // ... problema: ¿Qué pasa si cambio de BD? Debo editar código
}

// ProductividadRepositoryImpl.java
public void crearRegistro(ProductividadRegistro registro) {
    // Mismo problema: Hardcoding
    String sql = "EXEC [JovyDataADW_DEV].[dbo].[PROD_IProductivityRecords] @xml = ?";
    // ... problema: ¿Debo cambiar todos los archivos manualmente?
}

// PrinterRepositoryImpl.java
public void crearImpresora(PrinterDTO printer) {
    // Mismo problema: Hardcoding
    SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
        .withProcedureName("MASTER_INPRINT");  // ← Quemado aquí
}
```

**Problemas:**
1. 🔴 **Repetición**: Escribimos la BD muchas veces
2. 🔴 **Cambios difíciles**: Cambiar de BD (DEV → PROD) requiere editar múltiples archivos
3. 🔴 **Poco mantenible**: Si hay 50 SPs, cambiar el nombre de la BD es un dolor de cabeza
4. 🔴 **Fácil de olvidar**: Alguien olvida actualizar un archivo y boom... error en PROD

---

### AHORA (Con patrón centralizado)
```dotenv
# .env - Un único lugar para toda la configuración
SPS_MAIN_DATABASE=JovyDataADW_DEV
SPS_EMPLEADOS_NAME=MASTER_GPDK_1_DEV
SPS_PRODUCTIVIDAD_NAME=PROD_IProductivityRecords
SPS_PRINTER_CREATE=MASTER_INPRINT
```

**Ventajas:**
1.  Una sola variable de BD principal
2.  Cambiar de ambiente es un cambio único
3.  Código limpio, sin hardcoding
4.  Fácil de entender y mantener

---

##  Principios Clave

-  **Una sola BD principal**: `SPS_MAIN_DATABASE` para evitar repetición
-  **Cada SP solo define su nombre**: La BD se hereda automáticamente
-  **Flexible**: Un SP puede tener su propia BD si lo necesita
-  **Sin hardcoding**: Todo configurable por variables de entorno
-  **Escalable**: Agregar nuevos SPs es trivial

---

## 1. Estructura de Configuración

### Flujo de Configuración

```
┌─────────────────────────────────────────┐
│    Archivo .env (Variables de entorno)  │
│  ─────────────────────────────────────  │
│  SPS_MAIN_DATABASE=JovyDataADW_DEV      │
│  SPS_EMPLEADOS_NAME=MASTER_GPDK_1_DEV   │
└────────────┬────────────────────────────┘
             │ (Spring Boot carga)
             ▼
┌─────────────────────────────────────────┐
│     application.properties              │
│  ─────────────────────────────────────  │
│  sps.main-database=${SPS_MAIN_DATABASE} │
│  sps.empleados.name=${SPS_EMPLEADOS_... │
└────────────┬────────────────────────────┘
             │ (@ConfigurationProperties)
             ▼
┌─────────────────────────────────────────┐
│      StoredProceduresProperties.java    │
│  ─────────────────────────────────────  │
│  mainDatabase = "JovyDataADW_DEV"       │
│  empleados.name = "MASTER_GPDK_1_DEV"   │
└────────────┬────────────────────────────┘
             │ (@Autowired en repositorios)
             ▼
┌─────────────────────────────────────────┐
│      Código de tu aplicación            │
│  ─────────────────────────────────────  │
│  spProps.getEmpleados()                 │
│    .buildExecQuery(parametros)          │
│  → EXEC [BD].[dbo].[SP] parametros      │
└─────────────────────────────────────────┘
```

### .env
```dotenv
# Base de datos principal (usada por la mayoría de SPs)
#  IMPORTANTE: Cambiar este valor afecta a TODOS los SPs que no tengan su propia BD
SPS_MAIN_DATABASE=JovyDataADW_DEV

# Empleados - Solo el nombre del SP
# La BD se hereda automáticamente de SPS_MAIN_DATABASE
SPS_EMPLEADOS_NAME=MASTER_GPDK_1_DEV

# Productividad - Solo el nombre del SP
SPS_PRODUCTIVIDAD_NAME=PROD_IProductivityRecords

# Impresoras - Solo nombres de SPs
SPS_PRINTER_CREATE=MASTER_INPRINT
SPS_PRINTER_UPDATE=MASTER_UPRINT
SPS_PRINTER_DELETE=MASTER_DPRINT
SPS_PRINTER_DELETE_DEFINITIVO=MASTER_DDPRINT

# Autenticación - Solo nombres de SPs
SPS_AUTH_LOGIN=MASTER_VLogin
SPS_AUTH_VALIDATE_TOKEN=MASTER_VToken

# (Opcional) Si algún SP usa otra BD:
# Descomenta y define si tu SP necesita una BD diferente
# SPS_REPORTES_DATABASE=OtraBase_DEV
# SPS_REPORTES_NAME=SP_REPORTE
```

### application.properties
```properties
# BD principal - Se carga desde la variable de entorno
# Si SPS_MAIN_DATABASE no está definida, usa el valor por defecto
sps.main-database=${SPS_MAIN_DATABASE:JovyDataADW_DEV}

# Empleados 
# name: Siempre obligatorio (el nombre del SP)
# database: Usa SPS_EMPLEADOS_DATABASE si existe, sino usa SPS_MAIN_DATABASE
sps.empleados.name=${SPS_EMPLEADOS_NAME:MASTER_GPDK_1_DEV}
sps.empleados.database=${SPS_EMPLEADOS_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}

# Productividad
sps.productividad.name=${SPS_PRODUCTIVIDAD_NAME:PROD_IProductivityRecords}
sps.productividad.database=${SPS_PRODUCTIVIDAD_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}

# Impresoras
sps.printer.create-sp=${SPS_PRINTER_CREATE:MASTER_INPRINT}
sps.printer.update-sp=${SPS_PRINTER_UPDATE:MASTER_UPRINT}
sps.printer.delete-sp=${SPS_PRINTER_DELETE:MASTER_DPRINT}
sps.printer.delete-definitivo-sp=${SPS_PRINTER_DELETE_DEFINITIVO:MASTER_DDPRINT}
sps.printer.database=${SPS_PRINTER_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}

# Autenticación
sps.auth.login-sp=${SPS_AUTH_LOGIN:MASTER_VLogin}
sps.auth.validate-token-sp=${SPS_AUTH_VALIDATE_TOKEN:MASTER_VToken}
sps.auth.database=${SPS_AUTH_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}
```

**¿Por qué esta estructura?**
- Cada SP define su nombre (obligatorio)
- La BD viene del entorno (variable externa)
- Si un SP necesita otra BD, puede override-it

---

## 2. Cómo Funciona Internamente

### StoredProceduresProperties.java - Explicado

```java
@Configuration
@ConfigurationProperties(prefix = "sps")
public class StoredProceduresProperties {
    //  BD principal que todos comparten
    private String mainDatabase;  // ← Se carga de sps.main-database (application.properties)
    
    // Configuraciones de módulos específicos
    private SpConfig empleados = new SpConfig();
    private SpConfig productividad = new SpConfig();
    private PrinterSPConfig printer = new PrinterSPConfig();
    private AuthSPConfig auth = new AuthSPConfig();
    
    // Getters para acceder desde los repositorios
    public String getMainDatabase() {
        return mainDatabase;
    }
    
    public SpConfig getEmpleados() {
        return empleados;  // ← Retorna config de empleados
    }
}
```

**¿Por qué una clase de configuración?**
- Spring Boot carga automáticamente desde application.properties
- Evitamos hardcoding de valores
- Fácil de testear y mockear
- Type-safe (no strings sueltos)

### 🔧 SpConfig - Cómo Funciona el "Fallback" a BD Principal

```java
public class SpConfig {
    private String database;  // Null por defecto
    private String name;      // Nombre del SP
    
    public String getDatabase() {
        // LÓGICA CLAVE: Si este SP tiene su propia BD, usarla
        // Si no, usar la BD principal
        return (database != null && !database.isEmpty()) ? database : mainDatabase;
    }
    
    public String buildExecQuery(String parameters) {
        // Construye la consulta EXEC completa
        // Ejemplo: EXEC [JovyDataADW_DEV].[dbo].[MASTER_GPDK_1_DEV] @param1 = ?
        return String.format("EXEC [%s].[dbo].[%s] %s", 
            this.getDatabase(),  // ← Obtiene BD correcta (propia o principal)
            this.name,           // ← Nombre del SP
            parameters           // ← Parámetros del query
        );
    }
}
```

**¿Por qué este diseño?**
- Un SP puede tener su propia BD si lo necesita
- Si no la tiene, usa la principal (evita repetición)
- Flexible pero con fallback seguro

---

## 3. Uso en los Repositorios

###  EmpleadoRepositoryImpl - Paso a Paso

```java
@Repository
public class EmpleadoRepositoryImpl implements EmpleadoRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    //  Inyectar la configuración centralizada
    @Autowired
    private StoredProceduresProperties spProps;
    
    @Override
    public List<Empleado> obtenerEmpleados() {
        // PASO 1: Obtener la configuración de empleados
        // spProps.getEmpleados() retorna el SpConfig de empleados
        
        // PASO 2: Construir la consulta
        String sql = spProps.getEmpleados()
            .buildExecQuery("@toDay = NULL, @idEmployed = NULL");
        
        // Resultado: "EXEC [JovyDataADW_DEV].[dbo].[MASTER_GPDK_1_DEV] @toDay = NULL, @idEmployed = NULL"
        
        // PASO 3: Ejecutar
        String jsonResponse = jdbcTemplate.queryForObject(sql, String.class);
        
        // PASO 4: Procesar respuesta...
        return mapearResultado(jsonResponse);
    }
}
```

**¿Qué sucede internamente?**
1. Spring inyecta `StoredProceduresProperties` (cargada desde application.properties)
2. Llamamos a `spProps.getEmpleados()` → obtiene config de empleados
3. Llamamos a `.buildExecQuery(...)` → construye `EXEC [BD].[dbo].[SP] parámetros`
4. Ejecutamos la consulta con JdbcTemplate

### 🟡 ProductividadRegistroRepositoryImpl - Alternativa Explícita

```java
public ProductividadRegistro crearRegistro(ProductividadRegistro registro) {
    String xml = construirXmlParaSP(registro);
    
    // En lugar de usar buildExecQuery, podemos ser explícitos:
    String database = spProps.getProductividad().getDatabase();  // Obtiene BD
    String spName = spProps.getProductividad().getName();        // Obtiene nombre
    
    // Construimos manualmente (más control)
    String sql = String.format("EXEC [%s].[dbo].[%s] @xml = ?", database, spName);
    
    // sql = "EXEC [JovyDataADW_DEV].[dbo].[PROD_IProductivityRecords] @xml = ?"
    
    String jsonResult = jdbcTemplate.queryForObject(sql, String.class, xml);
    // Procesar resultado...
}
```

**¿Cuándo usar uno u otro?**
- `buildExecQuery()` → Más simple, recomendado para la mayoría
- Método explícito → Cuando necesitas más control del query

### 🔵 PrinterRepositoryImpl - Con SimpleJdbcCall

```java
@Repository
public class PrinterRepositoryImpl implements PrinterRepository {
    
    private final DataSource dataSource;
    
    @Autowired
    private StoredProceduresProperties spProps;
    
    private void initializeSimpleJdbcCalls() {
        // Obtener los NOMBRES de los SPs desde configuración
        String createSpName = spProps.getPrinter().getCreateSp();  // MASTER_INPRINT
        String updateSpName = spProps.getPrinter().getUpdateSp();  // MASTER_UPRINT
        
        // Crear SimpleJdbcCall con el nombre del SP (no con hardcoding)
        this.crearImpresoraCall = new SimpleJdbcCall(dataSource)
            .withProcedureName(createSpName)  // ← Viene de config
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("PRI002", Types.VARCHAR),
                new SqlParameter("PRI003", Types.VARCHAR)
                // ... más parámetros
            );
    }
}
```

**¿Por qué `SimpleJdbcCall` es útil aquí?**
- Maneja automáticamente parámetros y tipos
- Mejor para SPs complejos con OUT parameters
- Pero aún así usamos la configuración centralizada

---

## 4. Cómo Agregar un Nuevo SP

### Caso A: SP de la BD Principal

**¿Por qué es recomendado?**
- Menos configuración
- Si todos usan la misma BD, es innecesario repetir

**Paso 1: Agregar en .env**
```dotenv
# Variable de entorno del nombre del SP
SPS_REPORTES_NAME=SP_REPORTES
```

**Paso 2: Agregar en application.properties**
```properties
# El nombre viene del entorno
sps.reportes.name=${SPS_REPORTES_NAME:SP_REPORTES}
# La BD viene de la principal (fallback automático)
sps.reportes.database=${SPS_REPORTES_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}
```

**Paso 3: Agregar en StoredProceduresProperties.java**
```java
private SpConfig reportes = new SpConfig();

public SpConfig getReportes() { 
    return reportes; 
}

public void setReportes(SpConfig reportes) { 
    this.reportes = reportes; 
}
```

**Paso 4: Usar en tu repositorio nuevo**
```java
public class ReportesRepositoryImpl implements ReportesRepository {
    @Autowired
    private StoredProceduresProperties spProps;
    
    public List<Reporte> obtenerReportes(String fechaIni, String fechaFin) {
        String sql = spProps.getReportes()
            .buildExecQuery("@fechaIni = ?, @fechaFin = ?");
        
        String jsonResponse = jdbcTemplate
            .queryForObject(sql, new Object[]{fechaIni, fechaFin}, String.class);
        
        return mapearResultado(jsonResponse);
    }
}
```

### Caso B: SP de Otra BD (Cuando es necesario)

**¿Cuándo usar?**
- Tu SP está en otra base de datos
- Ej: SP en `OtraBase_DEV` que la principal es `JovyDataADW_DEV`

**Paso 1: Agregar en .env**
```dotenv
# Define BD diferente + nombre del SP
SPS_OTRO_DATABASE=OtraBase_DEV
SPS_OTRO_NAME=SP_OTRO
```

**Paso 2: Agregar en application.properties**
```properties
# Especifica su propia BD (no hereda de principal)
sps.otro.database=${SPS_OTRO_DATABASE:OtraBase_DEV}
sps.otro.name=${SPS_OTRO_NAME:SP_OTRO}
```

**Paso 3 y 4**: Igual al Caso A

---

## 5. Cambiar de Ambiente (DEV/PROD)

### Antes vs Después

** Antes (sin patrón):**
```bash
# Tendrías que cambiar en:
# EmpleadoRepositoryImpl.java - línea X
# ProductividadRepositoryImpl.java - línea Y
# PrinterRepositoryImpl.java - línea Z
# ... 50+ archivos más

# Propenso a errores, olvidar algunos cambios
```

** Después (con patrón):**
```dotenv
# Un único cambio:

# .env (DEV)
SPS_MAIN_DATABASE=JovyDataADW_DEV

# .env (PROD)
SPS_MAIN_DATABASE=JovyDataADW_PROD
```

 **Automáticamente todos los SPs usan la nueva BD**

---

## 6. Referencia Rápida

| Módulo | Variable Principal | Nombres de SPs |
|--------|--------------------|---|
| **Empleados** | `SPS_MAIN_DATABASE` | `SPS_EMPLEADOS_NAME` |
| **Productividad** | `SPS_MAIN_DATABASE` | `SPS_PRODUCTIVIDAD_NAME` |
| **Impresoras** | `SPS_MAIN_DATABASE` | `SPS_PRINTER_CREATE`, `_UPDATE`, `_DELETE`, `_DELETE_DEFINITIVO` |
| **Autenticación** | `SPS_MAIN_DATABASE` | `SPS_AUTH_LOGIN`, `SPS_AUTH_VALIDATE_TOKEN` |

---

## 7. Ventajas 
| Ventaja | Explicación |
|---------|----------|
| **Menos repetición** | Una sola BD principal en lugar de N veces |
| **Más limpio** | Variables de entorno organizadas y claras |
| **Flexible** | Un SP puede tener su propia BD si lo necesita |
| **Predecible** | `getDatabase()` siempre retorna lo correcto |
| **Escalable** | Agregar SPs es solo agregar 3 líneas de código |
| **Fácil de cambiar** | DEV → PROD es un único cambio |
| **Type-safe** | Java evalúa tipos, no strings sueltos |
| **Testeable** | Puedes mockear fácilmente la configuración |

¡Listo! La configuración es limpia, mantenible y escalable.
```dotenv
# Base de datos principal (usada por la mayoría de SPs)
SPS_MAIN_DATABASE=JovyDataADW_DEV

# Empleados
SPS_EMPLEADOS_NAME=MASTER_GPDK_1_DEV

# Productividad
SPS_PRODUCTIVIDAD_NAME=PROD_IProductivityRecords

# Impresoras
SPS_PRINTER_CREATE=MASTER_INPRINT
SPS_PRINTER_UPDATE=MASTER_UPRINT
SPS_PRINTER_DELETE=MASTER_DPRINT
SPS_PRINTER_DELETE_DEFINITIVO=MASTER_DDPRINT

# Autenticación
SPS_AUTH_LOGIN=MASTER_VLogin
SPS_AUTH_VALIDATE_TOKEN=MASTER_VToken

# (Opcional) Si algún SP usa otra BD:
# SPS_REPORTES_DATABASE=OtraBase_DEV
# SPS_REPORTES_NAME=SP_REPORTE
```

### application.properties
```properties
# BD principal
sps.main-database=${SPS_MAIN_DATABASE:JovyDataADW_DEV}

# Empleados (usa mainDatabase por defecto)
sps.empleados.name=${SPS_EMPLEADOS_NAME:MASTER_GPDK_1_DEV}
sps.empleados.database=${SPS_EMPLEADOS_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}

# Productividad (usa mainDatabase por defecto)
sps.productividad.name=${SPS_PRODUCTIVIDAD_NAME:PROD_IProductivityRecords}
sps.productividad.database=${SPS_PRODUCTIVIDAD_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}

# Impresoras (usa mainDatabase por defecto)
sps.printer.create-sp=${SPS_PRINTER_CREATE:MASTER_INPRINT}
sps.printer.update-sp=${SPS_PRINTER_UPDATE:MASTER_UPRINT}
sps.printer.delete-sp=${SPS_PRINTER_DELETE:MASTER_DPRINT}
sps.printer.delete-definitivo-sp=${SPS_PRINTER_DELETE_DEFINITIVO:MASTER_DDPRINT}
sps.printer.database=${SPS_PRINTER_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}

# Autenticación (usa mainDatabase por defecto)
sps.auth.login-sp=${SPS_AUTH_LOGIN:MASTER_VLogin}
sps.auth.validate-token-sp=${SPS_AUTH_VALIDATE_TOKEN:MASTER_VToken}
sps.auth.database=${SPS_AUTH_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}
```

---

## 2. Cómo Funciona Internamente

### StoredProceduresProperties.java
```java
@Configuration
@ConfigurationProperties(prefix = "sps")
public class StoredProceduresProperties {
    private String mainDatabase;  // ← BD principal
    
    public String getMainDatabase() {
        return mainDatabase;
    }
    
    public SpConfig getEmpleados() {
        return empleados;
    }
    // ... más getters
}
```

### SpConfig (obtiene BD automáticamente)
```java
public class SpConfig {
    private String database;  // Null por defecto
    private String name;
    
    public String getDatabase() {
        // Si tiene BD propia, usar esa; sino usar mainDatabase
        return (database != null && !database.isEmpty()) ? database : mainDatabase;
    }
    
    public String buildExecQuery(String parameters) {
        return String.format("EXEC [%s].[dbo].[%s] %s", 
            this.getDatabase(), this.name, parameters);
    }
}
```

---

## 3. Uso en los Repositorios

### EmpleadoRepositoryImpl
```java
@Autowired
private StoredProceduresProperties spProps;

public List<Empleado> obtenerEmpleados() {
    String sql = spProps.getEmpleados()
        .buildExecQuery("@toDay = NULL, @idEmployed = NULL");
    // Resultado: EXEC [JovyDataADW_DEV].[dbo].[MASTER_GPDK_1_DEV] @toDay = NULL, @idEmployed = NULL
    
    String jsonResponse = jdbcTemplate.queryForObject(sql, String.class);
}
```

### ProductividadRegistroRepositoryImpl
```java
public ProductividadRegistro crearRegistro(ProductividadRegistro registro) {
    String xml = construirXmlParaSP(registro);
    
    String database = spProps.getProductividad().getDatabase();  // ← Obtiene mainDatabase
    String spName = spProps.getProductividad().getName();
    
    String sql = String.format("EXEC [%s].[dbo].[%s] @xml = ?", database, spName);
    String jsonResult = jdbcTemplate.queryForObject(sql, String.class, xml);
}
```

### PrinterRepositoryImpl (SimpleJdbcCall)
```java
private void initializeSimpleJdbcCalls() {
    String createSpName = spProps.getPrinter().getCreateSp();  // ← Nombre del SP
    
    this.crearImpresoraCall = new SimpleJdbcCall(dataSource)
        .withProcedureName(createSpName)
        .declareParameters(...);
}
```

### AuthenticationUtil
```java
public AuthenticationUtil(DataSource dataSource, StoredProceduresProperties spProps) {
    String loginSpName = spProps.getAuth().getLoginSp();  // ← Nombre del SP
    
    this.loginCall = new SimpleJdbcCall(dataSource)
        .withProcedureName(loginSpName)
        .declareParameters(...);
}
```

---

## 4. Cómo Agregar un Nuevo SP

### Caso A: SP de la BD Principal (Recomendado)
**1. Agregar en .env:**
```dotenv
SPS_REPORTES_NAME=SP_REPORTES
```

**2. Agregar en application.properties:**
```properties
sps.reportes.name=${SPS_REPORTES_NAME:SP_REPORTES}
sps.reportes.database=${SPS_REPORTES_DATABASE:${SPS_MAIN_DATABASE:JovyDataADW_DEV}}
```

**3. Agregar en StoredProceduresProperties.java:**
```java
private SpConfig reportes = new SpConfig();

public SpConfig getReportes() { 
    return reportes; 
}

public void setReportes(SpConfig reportes) { 
    this.reportes = reportes; 
}
```

**4. Usar en el repositorio:**
```java
String sql = spProps.getReportes()
    .buildExecQuery("@fechaIni = ?, @fechaFin = ?");
```

### Caso B: SP de Otra BD
**1. Agregar en .env:**
```dotenv
SPS_OTRO_DATABASE=OtraBase_DEV
SPS_OTRO_NAME=SP_OTRO
```

**2. Agregar en application.properties:**
```properties
sps.otro.database=${SPS_OTRO_DATABASE:OtraBase_DEV}
sps.otro.name=${SPS_OTRO_NAME:SP_OTRO}
```

**3. Resto igual al Caso A**

---