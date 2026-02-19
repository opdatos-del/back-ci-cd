package com.example.backend_jovyweb.modules.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilidad para Rate Limiting de intentos de login.
 * Previene ataques de fuerza bruta limitando intentos por IP.
 * 
 * Nota: Implementación en memoria sin persistencia en BD.
 * Para sistemas distribuidos, considerar usar Redis.
 */
@Component
public class RateLimiterUtil {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterUtil.class);

    @Value("${auth.rate.limit.attempts:5}")
    private int maxAttempts;

    @Value("${auth.rate.limit.window:60000}")
    private long windowMs; // Ventana de tiempo en milisegundos

    // Map de intentos: IP -> Queue de timestamps
    private final Map<String, Queue<Long>> attemptTracker = new ConcurrentHashMap<>();

    // Map de bloqueos: IP -> timestamp de desbloqueo
    private final Map<String, LocalDateTime> blockedIps = new ConcurrentHashMap<>();

    /**
     * Verifica si una IP está bloqueada por Rate Limiting.
     *
     * @param ipAddress dirección IP a verificar
     * @return true si está bloqueada, false si puede intentar
     */
    public boolean isBlocked(String ipAddress) {
        LocalDateTime unblockTime = blockedIps.get(ipAddress);
        if (unblockTime != null) {
            if (LocalDateTime.now().isAfter(unblockTime)) {
                // Tiempo de bloqueo expiró
                blockedIps.remove(ipAddress);
                attemptTracker.remove(ipAddress);
                logger.info("Desbloqueo de IP: {}", ipAddress);
                return false;
            }
            logger.warn("IP bloqueada (Rate Limit): {}", ipAddress);
            return true;
        }
        return false;
    }

    /**
     * Registra un intento fallido de login.
     * Si se excede el límite, bloquea la IP.
     *
     * @param ipAddress dirección IP
     * @return true si se bloqueó la IP, false si aún hay intentos disponibles
     */
    public boolean recordFailedAttempt(String ipAddress) {
        long now = System.currentTimeMillis();

        // Obtener o crear la cola de intentos para esta IP
        Queue<Long> attempts = attemptTracker.computeIfAbsent(ipAddress, k -> new LinkedList<>());

        // Limpiar intentos antiguos (fuera de la ventana de tiempo)
        while (!attempts.isEmpty() && (now - attempts.peek()) > windowMs) {
            attempts.poll();
        }

        // Agregar nuevo intento
        attempts.offer(now);

        // Verificar si se excedió el límite
        if (attempts.size() >= maxAttempts) {
            // Bloquear por 15 minutos
            LocalDateTime unblockTime = LocalDateTime.now().plus(15, ChronoUnit.MINUTES);
            blockedIps.put(ipAddress, unblockTime);
            logger.warn("IP bloqueada por Rate Limiting: {} - Desbloqueará en: {}",
                    ipAddress, unblockTime);
            return true;
        }

        logger.debug("Intento fallido registrado para IP: {} - Intentos: {}/{}",
                ipAddress, attempts.size(), maxAttempts);
        return false;
    }

    /**
     * Limpia los intentos de una IP después de login exitoso.
     *
     * @param ipAddress dirección IP con login exitoso
     */
    public void clearAttempts(String ipAddress) {
        attemptTracker.remove(ipAddress);
        logger.debug("Intentos limpiados para IP: {}", ipAddress);
    }

    /**
     * Obtiene el número de intentos restantes para una IP.
     *
     * @param ipAddress dirección IP
     * @return intentos restantes antes de bloqueo
     */
    public int getRemainingAttempts(String ipAddress) {
        if (isBlocked(ipAddress)) {
            return 0;
        }

        Queue<Long> attempts = attemptTracker.get(ipAddress);
        if (attempts == null) {
            return maxAttempts;
        }

        // Limpiar intentos antiguos
        long now = System.currentTimeMillis();
        while (!attempts.isEmpty() && (now - attempts.peek()) > windowMs) {
            attempts.poll();
        }

        return maxAttempts - attempts.size();
    }

    /**
     * Obtiene el tiempo de desbloqueo para una IP.
     *
     * @param ipAddress dirección IP
     * @return LocalDateTime de desbloqueo, o null si no está bloqueada
     */
    public LocalDateTime getUnblockTime(String ipAddress) {
        return blockedIps.get(ipAddress);
    }
}
