package com.example.backend_jovyweb.modules.auth.repository;

import com.example.backend_jovyweb.modules.auth.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad AuditLog.
 * Proporciona métodos de acceso a los registros de auditoría.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    /**
     * Encuentra todos los registros de auditoría de un empleado.
     *
     * @param employeeCode código del empleado
     * @return lista de registros de auditoría del empleado
     */
    List<AuditLog> findByEmployeeCode(Integer employeeCode);

    /**
     * Encuentra todos los registros de auditoría de un tipo de acción.
     *
     * @param action tipo de acción (LOGIN, LOGOUT, INSERT, UPDATE, DELETE)
     * @return lista de registros de auditoría de esa acción
     */
    List<AuditLog> findByAction(String action);

    /**
     * Encuentra todos los registros de auditoría en un rango de fechas.
     *
     * @param startDate fecha inicial
     * @param endDate   fecha final
     * @return lista de registros de auditoría en ese rango
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Encuentra todos los registros de auditoría de un empleado en un rango de
     * fechas.
     *
     * @param employeeCode código del empleado
     * @param startDate    fecha inicial
     * @param endDate      fecha final
     * @return lista de registros de auditoría filtrados
     */
    List<AuditLog> findByEmployeeCodeAndTimestampBetween(Integer employeeCode, LocalDateTime startDate,
            LocalDateTime endDate);
}
