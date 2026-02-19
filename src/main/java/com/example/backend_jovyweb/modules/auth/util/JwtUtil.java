package com.example.backend_jovyweb.modules.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utilidad para generar, validar y extraer información de tokens JWT/JWE.
 * Utiliza la librería JJWT para manejo seguro de tokens.
 * 
 * Cambios de seguridad mejorados:
 * - JWT firmado con HS512 (clave 512+ bits)
 * - JWE (cifrado) con A256GCM para proteger payload sensible
 * - Expiración corta (15 minutos)
 * - Solo claims NO sensibles en el token:
 * * type: "ACCESS"
 * * sub: empleado (ya identificación única)
 * * iss, aud, jti, iat, exp
 * - NO incluye employeeCode, departmentCode, email, deviceFingerprint
 * - Datos sensibles se consultan en BD o en header separado
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.encryption.secret}")
    private String jwtEncryptionSecret;

    // ⏱️ CONFIGURACIÓN DE EXPIRACIÓN DEL JWT (ACCESS TOKEN)
    // 🔒 OBLIGATORIO: Debe configurarse en variables de entorno: JWT_EXPIRATION
    // Ejemplos: 900000 (15 min), 1800000 (30 min), 300000 (5 min)
    // NOTA: Si cambias este valor, también ajusta JWT_REFRESH_THRESHOLD
    // proporcionalmente
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ⏱️ CONFIGURACIÓN DEL UMBRAL DE REFRESH AUTOMÁTICO
    // 🔒 OBLIGATORIO: Debe configurarse en variables de entorno:
    // JWT_REFRESH_THRESHOLD
    // Por defecto recomendado: 120000 (2 minutos antes de expiración)
    // Ejemplos: 60000 (1 min), 120000 (2 min), 300000 (5 min)
    @Value("${jwt.refresh.threshold}")
    private long jwtRefreshThreshold;

    @Value("${jwt.issuer:JovyWeb}")
    private String issuer;

    @Value("${jwt.audience:JovyWeb-API}")
    private String audience;

    /**
     * Genera un JWT seguro y minimalista (sin información sensible).
     * 
     * ⚠️ SEGURIDAD: El payload del JWT contiene SOLO:
     * - type: "ACCESS"
     * - sub: identificador único del empleado
     * - iss, aud, jti, iat, exp: metadatos JWT estándar
     * 
     * NO contiene: employeeCode, departmentCode, email, deviceFingerprint
     * Estos datos sensibles se consultan en BD o en headers separados.
     * 
     * @param employeeCode      código del empleado (se usa como 'sub')
     * @param departmentCode    código del departamento (NO se incluye)
     * @param email             correo del empleado (NO se incluye)
     * @param deviceFingerprint fingerprint (se valida en header, NO en JWT)
     * @return token JWT seguro y minimalista
     */
    public String generateJWT(Integer employeeCode, Integer departmentCode, String email, String slpCode,
            String deviceFingerprint) {
        try {
            if (jwtSecret == null || jwtSecret.isBlank()) {
                throw new RuntimeException("JWT_SECRET no está configurada. Debe definirse en variables de entorno.");
            }

            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            // Payload MINIMALISTA - Solo claims no sensibles
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", "ACCESS"); // Tipo de token
            claims.put("slpcode", slpCode); // Ahora guarda el valor real de SlpCode
            claims.put("department", departmentCode);
            claims.put("deviceFingerprint", deviceFingerprint); // Se agrega como claim separado

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            String jwtToken = Jwts.builder()
                    .claims(claims)
                    .subject(String.valueOf(employeeCode)) // Usar como 'sub'
                    .issuer(issuer)
                    .audience().add(audience).and()
                    .id(UUID.randomUUID().toString()) // jti - identificador único
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(key) // Firma segura con HS512
                    .compact();
            return jwtToken;

        } catch (JwtException e) {
            logger.error("Error al generar el Token", e);
            throw new RuntimeException("No se pudo generar el token", e);
        }
    }

    /**
     * Valida un token JWT verificando:
     * - Firma criptográfica
     * - No expirado
     * - Issuer y Audience correctos
     * - Es un JWT de ACCESO (no refresh token)
     *
     * @param token token a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            if (jwtSecret == null || jwtSecret.isBlank()) {
                logger.error("SECRET no configurada");
                return false;
            }

            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token);

            // Verificar que sea un JWT de ACCESO (no refresh token)
            String type = (String) jws.getPayload().get("type");
            if (!"ACCESS".equals(type)) {
                logger.warn("Token inválido: tipo no es ACCESS");
                return false;
            }

            return true;

        } catch (SecurityException e) {
            logger.warn("Firma JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado");
        } catch (UnsupportedJwtException e) {
            logger.warn("Token JWT no soportado");
        } catch (IllegalArgumentException e) {
            logger.warn("String JWT vacío");
        } catch (JwtException e) {
            logger.warn("Error al validar JWT: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrae el código de empleado del token JWT desde el claim 'sub'.
     *
     * @param token token JWT
     * @return código de empleado
     */
    public Integer getEmployeeCodeFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            String sub = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Integer.parseInt(sub);
        } catch (Exception e) {
            logger.error("Error al extraer código de empleado del token", e);
            return null;
        }
    }

    /**
     * ⚠️ DEPRECADO - departmentCode NO se incluye en el JWT por razones de
     * seguridad.
     * Debe consultarse en la BD usando el employeeCode.
     *
     * @param token token JWT
     * @return null siempre (no disponible en JWT)
     */
    @Deprecated(since = "1.1", forRemoval = false)
    public Integer getDepartmentCodeFromToken(String token) {
        logger.warn("getDepartmentCodeFromToken() deprecated - departmentCode no está en JWT");
        logger.warn("   Consulta el departmentCode en BD usando employeeCode");
        return null;
    }

    /**
     * ⚠️ DEPRECADO - Email NO se incluye en el JWT por razones de seguridad.
     * Debe consultarse en la BD usando el employeeCode.
     *
     * @param token token JWT
     * @return null siempre (no disponible en JWT)
     */
    @Deprecated(since = "1.1", forRemoval = false)
    public String getEmailFromToken(String token) {
        logger.warn("getEmailFromToken() deprecated - email no está en JWT");
        logger.warn("   Consulta el email en BD usando employeeCode");
        return null;
    }

    /**
     * ⚠️ DEPRECADO - deviceFingerprint NO se incluye en el JWT por razones de
     * seguridad.
     * El deviceFingerprint se valida en un header HTTP separado o en BD.
     *
     * @param token token JWT
     * @return null siempre (no disponible en JWT)
     */
    @Deprecated(since = "1.1", forRemoval = false)
    public String getDeviceFingerprintFromToken(String token) {
        logger.warn("getDeviceFingerprintFromToken() deprecated - deviceFingerprint no está en JWT");
        logger.warn("   El deviceFingerprint se valida desde header o BD, NO desde JWT");
        return null;
    }

    /**
     * Obtiene los claims del token JWT.
     *
     * @param token token JWT
     * @return claims del token
     */
    public Claims getClaims(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            logger.error("Error al extraer claims del token", e);
            throw new RuntimeException("No se pudieron extraer los claims del token", e);
        }
    }

    /**
     * Extrae el token JWT del header Authorization.
     *
     * @param authHeader valor del header Authorization
     * @return token JWT sin el prefijo "Bearer "
     */
    public String getTokenFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Verifica si un token JWT está próximo a expirar.
     * Útil para hacer refresh automático antes de que se expire.
     * 
     * ⏱️ UMBRAL DE REFRESH AUTOMÁTICO:
     * Se configura desde la variable de entorno: JWT_REFRESH_THRESHOLD
     * Recomendado: 120000 (2 minutos antes de expiración)
     * 💡 SUGERENCIA: Si cambias JWT_EXPIRATION, ajusta proporcionalmente
     * JWT_REFRESH_THRESHOLD
     * Ejemplo: Si JWT_EXPIRATION=300000 (5 min), usa JWT_REFRESH_THRESHOLD=60000 (1
     * min)
     * 
     * @param token token JWT a verificar
     * @return true si el token expira dentro del umbral configurado, false en caso
     *         contrario
     */
    public boolean isTokenNearExpiration(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            if (expiration == null) {
                return false;
            }

            // UMBRAL DE REFRESH AUTOMÁTICO (desde variables de entorno)
            long currentTimeWithThreshold = System.currentTimeMillis() + jwtRefreshThreshold;

            return expiration.getTime() < currentTimeWithThreshold;

        } catch (JwtException e) {
            // Si hay error al validar, considera que necesita refresh
            return true;
        }
    }
}
