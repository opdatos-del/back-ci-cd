package com.example.backend_jovyweb.modules.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilidad para generar y validar Device Fingerprint.
 * El fingerprint se calcula a partir de IP y User-Agent del cliente.
 * Esto previene que un token robado sea usado desde otro dispositivo.
 */
@Component
public class DeviceFingerprintUtil {

    private static final Logger logger = LoggerFactory.getLogger(DeviceFingerprintUtil.class);

    /**
     * Genera el fingerprint del dispositivo basado en IP y User-Agent.
     * Formato: SHA256(IP + User-Agent)
     *
     * @param ipAddress dirección IP del cliente
     * @param userAgent User-Agent del navegador
     * @return fingerprint en Base64
     */
    public String generateFingerprint(String ipAddress, String userAgent) {
        try {
            String input = ipAddress + "|" + userAgent;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error al generar device fingerprint", e);
            throw new RuntimeException("Error al generar fingerprint del dispositivo", e);
        }
    }

    /**
     * Extrae la IP real del cliente considerando proxies y load balancers.
     * Valida en orden: X-Forwarded-For, X-Real-IP, Remote Address
     *
     * @param request HttpServletRequest
     * @return dirección IP del cliente
     */
    public String extractClientIp(HttpServletRequest request) {
        // Verificar X-Forwarded-For (para proxies)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Tomar la primera IP (cliente original)
            return xForwardedFor.split(",")[0].trim();
        }

        // Verificar X-Real-IP (para nginx, etc)
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // Fallback: Remote Address
        return request.getRemoteAddr();
    }

    /**
     * Obtiene el User-Agent del request.
     *
     * @param request HttpServletRequest
     * @return User-Agent, o valor por defecto si no existe
     */
    public String extractUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "Unknown";
    }

    /**
     * Valida que el fingerprint del dispositivo coincida con el almacenado.
     * Si son diferentes, indica que el token se intenta usar desde otro
     * dispositivo.
     *
     * @param currentFingerprint fingerprint actual del cliente
     * @param storedFingerprint  fingerprint almacenado en el token
     * @return true si coinciden, false si son diferentes
     */
    public boolean validateFingerprint(String currentFingerprint, String storedFingerprint) {
        if (currentFingerprint == null || storedFingerprint == null) {
            logger.warn("Fingerprint null durante validación");
            return false;
        }
        return currentFingerprint.equals(storedFingerprint);
    }

    /**
     * Detecta si hay cambio de IP o User-Agent respecto al almacenado.
     * Retorna un objeto con los detalles de anomalía detectada.
     *
     * @param storedIp         IP almacenada
     * @param currentIp        IP actual
     * @param storedUserAgent  User-Agent almacenado
     * @param currentUserAgent User-Agent actual
     * @return objeto DeviceAnomaly con detalles del cambio
     */
    public DeviceAnomaly detectAnomalies(String storedIp, String currentIp,
            String storedUserAgent, String currentUserAgent) {
        DeviceAnomaly anomaly = new DeviceAnomaly();

        if (!storedIp.equals(currentIp)) {
            anomaly.setIpChanged(true);
            anomaly.setStoredIp(storedIp);
            anomaly.setCurrentIp(currentIp);
            logger.warn("Cambio de IP detectado: {} -> {}", storedIp, currentIp);
        }

        if (!storedUserAgent.equals(currentUserAgent)) {
            anomaly.setUserAgentChanged(true);
            anomaly.setStoredUserAgent(storedUserAgent);
            anomaly.setCurrentUserAgent(currentUserAgent);
            logger.warn("Cambio de User-Agent detectado");
        }

        return anomaly;
    }

    /**
     * Clase interna para reportar anomalías detectadas.
     */
    public static class DeviceAnomaly {
        private boolean ipChanged;
        private boolean userAgentChanged;
        private String storedIp;
        private String currentIp;
        private String storedUserAgent;
        private String currentUserAgent;

        public boolean hasAnomalies() {
            return ipChanged || userAgentChanged;
        }

        // Getters y Setters
        public boolean isIpChanged() {
            return ipChanged;
        }

        public void setIpChanged(boolean ipChanged) {
            this.ipChanged = ipChanged;
        }

        public boolean isUserAgentChanged() {
            return userAgentChanged;
        }

        public void setUserAgentChanged(boolean userAgentChanged) {
            this.userAgentChanged = userAgentChanged;
        }

        public String getStoredIp() {
            return storedIp;
        }

        public void setStoredIp(String storedIp) {
            this.storedIp = storedIp;
        }

        public String getCurrentIp() {
            return currentIp;
        }

        public void setCurrentIp(String currentIp) {
            this.currentIp = currentIp;
        }

        public String getStoredUserAgent() {
            return storedUserAgent;
        }

        public void setStoredUserAgent(String storedUserAgent) {
            this.storedUserAgent = storedUserAgent;
        }

        public String getCurrentUserAgent() {
            return currentUserAgent;
        }

        public void setCurrentUserAgent(String currentUserAgent) {
            this.currentUserAgent = currentUserAgent;
        }
    }
}
