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
 * Utilidad para generar y validar Refresh Tokens.
 * Los Refresh Tokens tienen expiración larga (7 días) y se usan para renovar
 * JWT.
 * Separa la responsabilidad: JWT corto (15min) + RefreshToken largo (7 días).
 */
@Component
public class RefreshTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenUtil.class);

    // 🔒 OBLIGATORIO: JWT_SECRET debe configurarse en variables de entorno
    @Value("${jwt.secret}")
    private String jwtSecret;

    // ⏱️ CONFIGURACIÓN DE EXPIRACIÓN DEL REFRESH TOKEN
    // 🔒 OBLIGATORIO: Debe configurarse en variables de entorno:
    // JWT_REFRESH_EXPIRATION
    // Ejemplos: 86400000 (1 día), 604800000 (7 días), 3600000 (1 hora)
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.issuer:JovyWeb}")
    private String issuer;

    /**
     * Genera un Refresh Token de larga duración.
     * El Refresh Token se usa para renovar JWT sin hacer login nuevamente.
     *
     * @param employeeCode      código del empleado
     * @param deviceFingerprint fingerprint del dispositivo
     * @return Refresh Token
     */
    public String generateRefreshToken(Integer employeeCode, String deviceFingerprint) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            Map<String, Object> claims = new HashMap<>();
            claims.put("employeeCode", employeeCode);
            claims.put("deviceFingerprint", deviceFingerprint);
            claims.put("type", "REFRESH"); // Identificar que es un refresh token

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

            String refreshToken = Jwts.builder()
                    .claims(claims)
                    .subject(String.valueOf(employeeCode))
                    .issuer(issuer)
                    .id(UUID.randomUUID().toString()) // Identificador único (jti)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(key) // Firma segura con HS512
                    .compact();

            logger.info("Refresh Token generado para empleado: {}", employeeCode);
            return refreshToken;

        } catch (JwtException e) {
            logger.error("Error al generar Refresh Token", e);
            throw new RuntimeException("No se pudo generar el Refresh Token", e);
        }
    }

    /**
     * Valida un Refresh Token.
     *
     * @param refreshToken token a validar
     * @return true si es válido, false en caso contrario
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken);

            // Verificar que sea un refresh token
            String type = (String) jws.getPayload().get("type");
            if (!"REFRESH".equals(type)) {
                logger.warn("Token inválido: no es un Refresh Token");
                return false;
            }

            return true;

        } catch (ExpiredJwtException e) {
            logger.warn("Refresh Token expirado");
        } catch (SecurityException e) {
            logger.error("Firma de Refresh Token inválida", e);
        } catch (MalformedJwtException e) {
            logger.error("Refresh Token malformado", e);
        } catch (IllegalArgumentException e) {
            logger.error("String Refresh Token vacío", e);
        } catch (JwtException e) {
            logger.error("Error al validar Refresh Token", e);
        }
        return false;
    }

    /**
     * Extrae el código de empleado del Refresh Token.
     *
     * @param refreshToken token
     * @return código de empleado
     */
    public Integer getEmployeeCodeFromRefreshToken(String refreshToken) {
        return (Integer) getClaims(refreshToken).get("employeeCode");
    }

    /**
     * Extrae el deviceFingerprint del Refresh Token.
     *
     * @param refreshToken token
     * @return deviceFingerprint
     */
    public String getDeviceFingerprintFromRefreshToken(String refreshToken) {
        return (String) getClaims(refreshToken).get("deviceFingerprint");
    }

    /**
     * Obtiene todos los claims de un Refresh Token.
     *
     * @param refreshToken token
     * @return claims
     */
    private Map<String, Object> getClaims(String refreshToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
        } catch (JwtException e) {
            logger.error("Error al extraer claims del Refresh Token", e);
            throw new RuntimeException("No se pudieron extraer los claims", e);
        }
    }
}
