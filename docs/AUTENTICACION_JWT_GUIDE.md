# Guía de Autenticación JWT - Backend JovyWeb

**Fecha**: 16 de febrero de 2026  
**Versión**: 1.0  
**Público**: Equipo de Desarrollo

---

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [¿Qué es JWT?](#qué-es-jwt)
3. [¿Qué es Spring Security?](#qué-es-spring-security)
4. [Flujo de Autenticación](#flujo-de-autenticación)
5. [Refresh Tokens](#refresh-tokens)
6. [Componentes Principales](#componentes-principales)
7. [Seguridad Implementada](#seguridad-implementada)
8. [Endpoints de Autenticación](#endpoints-de-autenticación)
9. [Preguntas Frecuentes](#preguntas-frecuentes)

---

## Introducción

El sistema de de autenticación actual, utiliza **JSON Web Tokens (JWT)** combinado con **Spring Security** para proteger las peticiones a la API. En lugar de mantener sesiones en el servidor (como los cookies tradicionales), generamos un token criptográfico que el cliente envía en cada petición.

**¿Por qué JWT?**
- Sin estado en el servidor (stateless)
- Escalable: funciona bien en sistemas distribuidos
- Seguro: cada token es criptográficamente firmado
- Portátil: funciona en cualquier plataforma

---

## ¿Qué es JWT?

### Definición Técnica

Un **JWT (JSON Web Token)** es un estándar abierto (RFC 7519) para crear tokens de acceso compactos y seguros. Es como un carné digital que contiene información sobre el usuario y está firmado criptográficamente.

### Estructura de un JWT

Un JWT consta de **3 partes separadas por puntos**:

```
header.payload.signature
```

**Ejemplo real (acortado):**
```
eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.
eyJ0eXBlIjoiQUNDRVNTIiwic3ViIjoiMTIzIn0.
k7vB9xK2mL3...
```

### Desglose de cada parte:

**1. Header (Encabezado)**
- Define qué algoritmo se usa para firmar el token
- En nuestro caso: HS512 (HMAC con SHA-512)
- También especifica que es un JWT

**2. Payload (Carga útil)**
- Contiene la información del usuario
- En nuestro caso incluye: `type` (tipo de token), `sub` (ID del empleado), `iss` (emisor), `aud` (audiencia), `jti` (ID único), `iat` (fecha de creación), `exp` (fecha de expiración)
- Por seguridad, NO incluimos: email, departamento, datos sensibles

**3. Signature (Firma)**
- Es el resultado de cifrar las 2 primeras partes con una clave secreta
- Garantiza que nadie ha modificado el token
- Si alguien intenta cambiar cualquier dato, la firma se invalida

### Ventaja de esta estructura:

El servidor puede verificar instantáneamente que el token es válido sin consultar una base de datos. Solo necesita descifrarlo y validar que la firma coincida.

---

## ¿Qué es Spring Security?

### Definición

**Spring Security** es un framework de seguridad muy poderoso que integra la autenticación y autorización en aplicaciones Spring Boot. Es como un guardaespaldas que protege la API.

### Lo que hace Spring Security:

1. **Filtra peticiones entrantes**: Intercepta todas las peticiones HTTP
2. **Valida tokens**: Verifica que el usuario está autenticado
3. **Autoriza acceso**: Comprueba permisos del usuario
4. **Protege contra ataques comunes**: CSRF, clickjacking, inyección SQL, etc.

### En nuestro caso:

En `SecurityConfig.java` le decimos a Spring Security:

- **Qué rutas son públicas**: `/api/auth/login`, `/api/auth/logout`, `/api/auth/validate`, `/swagger-ui`
- **Qué rutas necesitan autenticación**: `/api/**` (todas las demás)
- **Cómo está deshabilitado CSRF**: Porque usamos JWT, que es más seguro
- **Cómo está configurado CORS**: Solo permiten peticiones de ciertos orígenes

---

## Flujo de Autenticación

### 1️⃣ Login (Autenticación)

```
Cliente                              Backend
   |                                    |
   |-- POST /api/auth/login --------->  |
   |    {username, password}            |
   |                                    |
   |                            Valida credenciales
   |                            vs SP (MASTER_VLogin)
   |                                    |
   |<-- 200 OK con datos -------------- |
   |    JWT en cookie                   |
   |    Cookie: X-AUTH-TOKEN=jwt        |
   |                                    |
```

**Qué ocurre en el backend:**

1. Usuario envía su empleado y contraseña
2. Backend consulta el Stored Procedure `MASTER_VLogin` en SQL Server
3. Si es válido, genera un JWT con la información del usuario
4. También genera un "Refresh Token" (válido 7 días) para renovar la sesión
5. Devuelve el JWT en una cookie `HttpOnly` + `Secure` + `SameSite=Strict`
6. Guarda la sesión en memoria del backend (activeSessions)
7. Registra el evento en la auditoría (SYS_AuditLog)

**Seguridad en esta etapa:**
- Rate Limiting: Si hay más de 5 intentos fallidos en 60 segundos, bloquea por 15 minutos
- La contraseña solo se valida contra el SP, nunca se almacena
- Se registra la IP real del cliente (extrayendo de headers de proxy si es necesario)

---

### 2️⃣ Petición Protegida (Validación)

```
Cliente                              Backend
   |                                    |
   |-- GET /api/usuarios -------------> |
   |    Cookie: X-AUTH-TOKEN=jwt        |
   |                                    |
   |                            1. Interceptor extrae JWT
   |                            2. Valida firma criptográfica
   |                            3. Verifica expiración
   |                            4. Comprueba blacklist
   |                            5. Valida device fingerprint
   |                                    |
   |<-- 200 OK con datos -------------- |
   |    Acceso permitido                |
   |                                    |
```

**Qué ocurre en el backend:**

1. `JwtInterceptor` intercepta la petición
2. Extrae el JWT de la cookie `X-AUTH-TOKEN`
3. Valida la firma criptográfica (¿alguien lo modificó?)
4. Verifica que no esté expirado
5. Comprueba que no esté en la blacklist (no fue invalidado por logout)
6. Valida que haya una sesión activa para ese token
7. Opcionalmente, valida el device fingerprint (para detectar uso de otro navegador)
8. Si todo pasa, permite el acceso. Si falla, retorna 401

---

### 3️⃣ Logout (Invalida la sesión)

```
Cliente                              Backend
   |                                    |
   |-- POST /api/auth/logout ---------> |
   |    Cookie: X-AUTH-TOKEN=jwt        |
   |                                    |
   |                            1. Busca sesión del usuario
   |                            2. Invalida TODOS sus tokens
   |                            3. Elimina sesión
   |                            4. Envía cookie vacía
   |                                    |
   |<-- 200 OK + Clear-Cookie ---.----- |
   |    Cookie: X-AUTH-TOKEN=;max-age=0 |
   |    Sesión cerrada en todos los     |
   |    dispositivos del usuario        |
   |                                    |
```

**Qué ocurre en el backend:**

1. Busca la sesión activa del usuario
2. Invalida TODOS los tokens de ese usuario (en todos los navegadores/dispositivos)
3. Agrega los tokens a la blacklist
4. Elimina todas las sesiones del usuario
5. Envía un header para limpiar la cookie en el cliente
6. Registra el evento en auditoría

**Característica importante:** El logout es **global**. Si el usuario tiene abierta la sesión en 3 navegadores, todos se cierran simultáneamente.

---

## Refresh Tokens

### ¿Por qué Refresh Tokens?

El JWT tiene una **expiración corta (15 minutos)** por razones de seguridad. Si se roba un JWT, el daño está limitado a 15 minutos. Pero esto crea un problema:

**Problema:** Si el user está usando la app activamente, cada 15 minutos se le cierra la sesión y debe volver a hacer login. ¡Muy molesto!

**Solución:** **Refresh Tokens**

Un Refresh Token es un token de larga duración (7 días) que el cliente puede usar para obtener un nuevo JWT sin necesidad de hacer login de nuevo.

### Flujo de Renovación de JWT

```
Cliente (App)                          Backend
   |                                      |
   |--- Usando la app normalmente --------|
   |    Timer: JWT expira en 15 min       |
   |                                      |
   |-- En minuto 10: detecta JWT próximo--|
   |   a expirar (opcional check)         |
   |                                      |
   |-- POST /api/auth/refresh ----------> |
   |    Body: {refreshToken}              |
   |                                      |
   |                            1. Valida refresh token
   |                            2. Verifica device fingerprint
   |                            3. Genera nuevo JWT (15 min)
   |                            4. Invalida JWT anterior
   |                            5. Retorna nuevo JWT
   |                                      |
   |<-- 200 OK con nuevo JWT -------------|
   |    Cookie: X-AUTH-TOKEN=nuevo_jwt    |
   |                                      |
   |--- Sigue usando la app sin ----------|
   |    interrupciones por 15 min más     |
   |    (hasta que vuelva a expirar)      |
   |                                      |
```

### Comparativa: Con vs Sin Refresh Tokens

**SIN Refresh Tokens:**
```
Minuto 0:  Login exitoso, JWT generado (15 min)
Minuto 15: JWT expira → Usuario debe hacer login nuevamente
Minuto 30: JWT expira → Usuario debe hacer login nuevamente
Minuto 45: JWT expira → Usuario debe hacer login nuevamente
           ↓
           ¡MUY MOLESTO EN PRODUCCIÓN!
```

**CON Refresh Tokens:**
```
Minuto 0:    Login exitoso, JWT (15 min) + Refresh Token (7 días)
Minuto 15:   JWT expira → App automáticamente llama /refresh
Minuto 15:   Backend valida Refresh Token  → emite nuevo JWT (15 min)
Minuto 30:   Usuario sigue activo, sin interrupciones
Minuto 30:   JWT expira → App llama /refresh de nuevo
Minuto 45:   Usuario sigue activo, sin interrupciones
...
Día 7:       Refresh Token expira → Usuario debe hacer login nuevamente
             (pero esto es aceptable, necesita revalidar credenciales)
```

### Seguridad del Refresh Token

El Refresh Token NO está en el body de `/refresh`. Está en una **cookie HTTP-Only**:

```
Cookie HttpOnly + Secure + SameSite=Strict
├─ XSS Protection: JavaScript NO puede leerla
├─ CSRF Protection: SameSite bloquea ejecución desde otro sitio
├─ Man-in-the-Middle: Solo viaja por HTTPS (Secure flag)
└─ Revocación: Backend puede invalidarla en cualquier momento
```

**Validaciones en servidor:**
1. El Refresh Token debe estar válido (no expirado)
2. El device fingerprint debe coincidir (detecta robo en otro navegador)
3. El usuario debe tener una sesión activa

Si alguna validación falla → Rechaza el refresh → El usuario debe hacer login nuevamente

---

### Seguridad y Acceso Público del Endpoint `/refresh`

> **¿Por qué la ruta `/api/auth/refresh` es pública?**

- El endpoint de refresh **debe ser público** porque el JWT ya está expirado cuando se solicita la renovación. No se puede exigir un JWT válido para acceder a `/refresh`.
- La seguridad NO depende de proteger la ruta, sino de la validación estricta del refresh token en el backend.

#### ¿Puede alguien abusar del endpoint?
- **No.** El refresh token es un JWT firmado, con expiración y claims únicos.
- Solo se puede usar si:
  1. El refresh token es válido (firma y expiración)
  2. El device fingerprint coincide (protege contra robo en otro navegador/dispositivo)
  3. Hay una sesión activa para ese usuario
- Si alguien intenta usar un refresh token robado, solo funcionará si también roba la cookie httpOnly y el fingerprint coincide (muy difícil).

#### ¿Qué pasa si intentan fuerza bruta?
- Los refresh tokens son aleatorios, largos y firmados. No se pueden adivinar ni forzar.
- Si un refresh token es inválido, el backend simplemente lo rechaza (401).

#### Resumen
- **La ruta debe ser pública** para permitir renovar el JWT cuando ya expiró.
- **La seguridad está en la validación del refresh token, no en proteger la ruta.**
- Es el mismo principio que el login: la ruta es pública, pero solo accede quien tiene credenciales válidas.
---

## Componentes Principales

### 1. JwtUtil.java

**Responsabilidad:** Generar y validar JWT

**Lo que hace:**
- Genera nuevos tokens JWT con la información del usuario
- Valida que un JWT no ha sido modificado (verifica la firma)
- Extrae información del token (empleado, timestamp de creación, etc.)
- Verifica que no esté expirado

**Datos incluidos en el token:** Solo lo mínimo necesario
- ID del empleado (sub)
- Tipo de token: ACCESS o REFRESH
- Emisor y audiencia
- Timestamps de creación y expiración
- ID único del token (jti)

**Por qué NO incluimos datos sensibles en el JWT:**
- El JWT es portable y visible en el navegador (aunque HttpOnly)
- Cualquiera puede decodificar el JWT y leer su contenido
- Es mejor guardar datos en el servidor y referencias en el token

---

### 2. AuthenticationUtil.java

**Responsabilidad:** Comunicación con la base de datos

**Lo que hace:**
- Ejecuta el Stored Procedure `MASTER_VLogin` para validar credenciales
- Ejecuta el Stored Procedure `MASTER_VToken` para validaciones adicionales
- Extrae información del usuario de la respuesta del SP
- Valida permisos de acceso
---

### 3. AuthServiceImpl.java

**Responsabilidad:** Orquestar el flujo de autenticación

**Lo que hace:**
- Coordina el login: Rate Limiting → Autenticación → Generación de token → Auditoría
- Coordina logout: Invalida tokens → Limpia sesiones → Auditoría
- Valida que los tokens sean correctos en cada petición
- Gestiona la blacklist de tokens invalidados

**Rate Limiting:**
- Si hay 5 intentos fallidos en 60 segundos desde la misma IP, bloquea por 15 minutos
- Protege contra ataques de fuerza bruta
- Se resetea al login exitoso

---

### 4. JwtInterceptor.java

**Responsabilidad:** Interceptor de Spring que valida JWT en cada petición

**Lo que hace:**
- Extrae el JWT de la petición (desde cookie o header)
- Valida la firma criptográfica
- Verifica que no esté expirado
- Comprueba que no esté en la blacklist
- Valida que haya sesión activa
- Opcionalmente valida el device fingerprint
- Si algo falla, retorna 401

**Rutas excluidas (no protegidas):**
- `/api/auth/login` - Necesitas login para logarte
- `/api/auth/logout` - No necesitas validar token para logout
- `/api/auth/validate` - Endpoint público para verificar sesión
- `/swagger-ui/**` - Documentación de API
- `/v3/api-docs/**` - Esquema OpenAPI

---

### 5. DeviceFingerprintUtil.java

**Responsabilidad:** Generar identificador único del navegador/dispositivo

**Lo que hace:**
- Calcula un SHA256 usando la IP + User-Agent del cliente
- Almacena el fingerprint en la sesión del servidor
- En cada petición, compara el fingerprint enviado con el almacenado
- Si no coincide, puede rechazar la petición (si está configurado en BLOCK)

**¿Por qué es importante?**
- Si alguien roba la cookie, no podrá usarla desde otro navegador
- Si lo usa desde el mismo navegador… sí funcionaría (es una limitación conocida)
- Protege contra algunos escenarios de cookie theft

---

### 6. RateLimiterUtil.java

**Responsabilidad:** Prevenir fuerza bruta

**Lo que hace:**
- Registra intentos fallidos por IP
- Después de 5 intentos fallidos en 60 segundos, bloquea por 15 minutos
- Permite reintentos después del tiempo de bloqueo

**Ataques que previene:**
- Fuerza bruta contra contraseñas
- Ataques DDoS ligeros

---

### 7. RefreshTokenUtil.java

**Responsabilidad:** Generar tokens de refresco

**Lo que hace:**
- Genera un token secundario válido por 7 días
- Permite renovar el JWT sin re-logarse
- Tiene expiración más larga que el JWT

**Uso (no implementado aún pero preparado):**
- El frontend obtiene el refresh token en login
- Cuando el JWT expira (x min), envía el refresh token
- Backend valida el refresh token y genera un nuevo JWT
- El usuario sigue en sesión sin hacer login nuevamente

---

## Seguridad Implementada

### 1. Cookie HttpOnly + Secure + SameSite

```
Cookie: X-AUTH-TOKEN=jwt
- HttpOnly: No accesible desde JavaScript (previene XSS)
- Secure: Solo se envía por HTTPS
- SameSite=Strict: Solo se envía en peticiones del mismo sitio (previene CSRF)
- Max-Age: 24 horas de validez
```

**Ataques que previene:**
- XSS (Cross-Site Scripting)
- CSRF (Cross-Site Request Forgery)
- Man-in-the-middle (si HTTPS)

---

### 2. JWT Criptográficamente Firmado

- Algoritmo: HS512 (HMAC-SHA512)
- Clave secreta: 512 bits en base64 (muy fuerte)
- Si alguien modifica un carácter del JWT, la firma no coincide y se rechaza

---

### 3. Expiración de Tokens

- JWT: 15 minutos
- Refresh Token: 7 días
- Tokens invalidados más de 24 horas antigos se limpian automáticamente

**¿Por qué expiración corta en JWT?**
- Si se roba un JWT, el atacante solo tiene 15 minutos
- Después, necesita el refresh token para obtener uno nuevo
- Reduce la ventana de ataque

---

### 4. Validación de Device Fingerprint

- Genera SHA256(IP + User-Agent)
- Se valida en cada petición protegida
- Si alguien usa el JWT desde otra IP o navegador, puede ser detectado

---

### 5. Rate Limiting

- 5 intentos fallidos en 60 segundos = bloqueo de 15 minutos
- Protege contra fuerza bruta
- Registra la IP bloqueada

---

### 6. Auditoría Completa

Regist cada evento de autenticación en `SYS_AuditLog`:
- ID del empleado
- Tipo de evento (LOGIN, LOGOUT)
- IP real del cliente
- Timestamp exacto
- Tabla afectada
- Descripción del evento

---

### 7. CORS Restrictivo

Solo se permiten peticiones desde orígenes autorizados:
- `http://localhost:3000`
- `http://localhost:4200`
- `http://localhost:8080`

En producción, cambiar a los dominios reales de tu aplicación.

---

### 8. Headers de Seguridad

```
Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline'
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
X-Frame-Options: DENY
Cache-Control: no-cache, no-store
```

Protegen contra:
- Inyección de scripts
- Clickjacking
- Sniffing de contenido
- Cache de datos sensibles

---

## Endpoints de Autenticación

### 1. Login (`POST /api/auth/login`)

**Entrada:**
```json
{
  "username": "empleado123",
  "password": "micontraseña"
}
```

**Respuesta exitosa (200):**
```json
{
  "employeeCode": 0,
  "name": "Juan Pérez",
  "email": "juan@company.com",
  "departmentCode": 1,
  "accessNumber": 1,
  "slpCode": "SLP001",
  "message": "Login exitoso",
  "success": true
}
```

**Respuesta con error (401):**
```json
{
  "message": "Error de autenticación: Usuario o contraseña incorrectos",
  "success": false
}
```

**Qué ocurre tras 200 OK:**
- Se devuelve una cookie `X-AUTH-TOKEN` con el JWT
- El cliente NO debe guardar el token manualmente
- El navegador almacena la cookie automáticamente
- Las peticiones futuras envían la cookie automáticamente

---

### 2. Logout (`POST /api/auth/logout`)

**Entrada:**
Cookie con `X-AUTH-TOKEN` (automática del navegador)

**Respuesta exitosa (200):**
```json
{
  "message": "Cierre de Sesión Exitoso.",
  "success": true
}
```

**Qué ocurre:**
- Se invalidan TODOS los tokens del usuario
- Se envía header para limpiar la cookie en el cliente
- Cualquier petición posterior con ese JWT será rechazada

---

### 3. Validate (`GET /api/auth/validate`)

**Propósito:** Verificar si la sesión del usuario es válida

**Entrada:**
Cookie con `X-AUTH-TOKEN` (automática del navegador)

**Respuesta si válido (200):**
```json
{
  "message": "Sesión activa",
  "success": true
}
```

**Respuesta si inválido (401):**
```json
{
  "message": "Sesión inválida o expirada",
  "success": false
}
```

**Uso del frontend:**
- Llamar en cada carga de página
- Si retorna 401, redirigir a login
- Si retorna 200, permitir acceso

---

### 4. Refresh (`POST /api/auth/refresh`)

**Propósito:** Renovar el JWT usando un Refresh Token (antes de que expire)

**Entrada:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
}
```

**Respuesta si exitoso (200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": null,
  "message": "Token JWT renovado exitosamente",
  "success": true
}
```

**Respuesta si falla (401):**
```json
{
  "token": null,
  "refreshToken": null,
  "message": "Error al renovar token: Refresh Token inválido o expirado. Debe hacer login nuevamente.",
  "success": false
}
```

**Qué ocurre en el backend:**
1. Valida que el Refresh Token sea válido (no expirado)
2. Verifica que el device fingerprint coincida
3. Confirma que hay una sesión activa para el usuario
4. Genera un nuevo JWT (válido 15 minutos)
5. Invalida el JWT anterior
6. Almacena la nueva sesión
7. Retorna el nuevo JWT en cookie

**Uso del frontend (pseudocódigo):**
```javascript
// Verificar si JWT está próximo a expirar (menos de 5 minutos)
if (jwtExpiresSoon()) {
  POST /api/auth/refresh { refreshToken }
  // Si 200: continuar sin interrupción
  // Si 401: redirigir a login
}
```

---

### 5. Debug Token (`GET /api/auth/debug/token`)

**Propósito:** Ver detalles del token actual (solo desarrollo)

**Entrada:**
Cookie con `X-AUTH-TOKEN`

**Respuesta:**
```json
{
  "timestamp": "2026-02-16T12:05:30.123",
  "ip_address": "10.0.0.50",
  "token_preview": "eyJhbGciOiJIUzUxMi...",
  "token_length": 412,
  "is_valid": "SÍ",
  "user_found": "SÍ",
  "employee_code": 123,
  "employee_name": "Juan Pérez",
  "employee_email": "juan@company.com",
  "is_session_active": "SÍ",
  "login_time": "2026-02-16T12:00:00"
}
```

---

## Preguntas Frecuentes

### ¿El tokens viaja encriptado o solo firmado?

**Respuesta:** Solo **firmado**, no encriptado. Esto significa:
- Cualquiera puede leer el contenido del JWT (Base64 es decodificable)
- Pero NO puede modificarlo sin que se detecte (está firmado)
- Es por eso que NO almacenamos datos sensibles en el JWT

### ¿Es seguro si alguien roba el token?

**Respuesta:** Depende:
- **Sí es seguro en muchos aspectos**: El JWT solo vive 15 minutos
- **No es 100% seguro**: Si es robado en los primeros 15 minutos, el atacante tenga acceso
- **Mitigación**: Device fingerprint detectaría uso desde otro navegador

**Medidas a tomar:**
- Usar HTTPS siempre (evita robo en tránsito)
- Detectar y alertar de actividad sospechosa
- Implementar logout automático tras inactividad
- Usar device fingerprint como protección adicional

### ¿Por qué JWT y no sesiones en el servidor?

**JWT (Sin estado):**
- ✅ Escalable: No necesita sincronización entre servidores
- ✅ Rápido: No consulta BD en cada petición
- ✅ Portátil: Funciona en microservicios
- ❌ Menos control: No puedes "forzar" logout al instante

**Sesiones en servidor (Con estado):**
- ✅ Más control: Puedes invalidar al instante
- ✅ Más seguro: Solo la referencia viaja al cliente
- ❌ Menos escalable: Necesita sincronización
- ❌ Más lento: Consulta BD cada vez

**Nuestro enfoque (híbrido):**
- Usamos JWT (sin estado)
- Pero mantenemos sesiones en memoria (control)
- Al logout, invalidamos todos los tokens (fuerza)
- Combinamos lo mejor de ambos

### ¿Por qué hay dos tokens (JWT + Refresh)?

**JWT (Access Token):** 
- Corta duración (15 min)
- Se valida en cada petición
- Si se roba, solo funciona 15 minutos

**Refresh Token:**
- Larga duración (7 días)
- Se usa solo para renovar el JWT
- Se protege más que el Access Token
- No se envía en cada petición

Así combinamos seguridad (JWT corto) con comodidad (Refresh Token largo).

### ¿Qué es el Device Fingerprint?

**Definición:** Un identificador único del navegador/dispositivo basado en características que no cambian.

**Cálculo:** SHA256(IP + User-Agent)

**Ejemplo:**
- IP: `192.168.1.100`
- User-Agent: `Mozilla/5.0 (Windows... Chrome/120.0`
- Fingerprint: `a3b4c5d6e7f8g9h0... (64 caracteres)`

**¿Qué pasa si lo copias a otro navegador?**
- SI usas OTRO navegador → Fingerprint diferente (User-Agent diferente) = RECHAZADO
- SI usas el MISMO navegador desde OTRA IP → Fingerprint diferente (IP diferente) = RECHAZADO
- SI usas el MISMO navegador desde la MISMA IP → Fingerprint igual = ACEPTADO

---

**Documento creado**: 16 de febrero de 2026  
**Equipo**: Backend JovyWeb
**Estado**: Versión 1.1
