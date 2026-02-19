package com.example.backend_jovyweb.modules.auth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad AuditLog que mapea a la tabla SYS_AuditLog de la base de datos.
 * Registra todas las acciones realizadas en el sistema para auditoría y
 * trazabilidad.
 */
@Entity
@Table(name = "SYS_AuditLog", schema = "dbo")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AudL001")
    private Integer id;

    @Column(name = "AudL002", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    @Column(name = "AudL003", nullable = false)
    private Integer employeeCode; // Código del empleado que realizó la acción

    @Column(name = "AudL004", length = 20, nullable = false)
    private String action; // INSERT, UPDATE, DELETE, LOGIN, LOGOUT

    @Column(name = "AudL005", length = 100, nullable = false)
    private String tableName; // Nombre de la tabla afectada

    @Column(name = "AudL006", length = 100, nullable = false)
    private String recordId; // ID o Clave Primaria del registro afectado

    @Column(name = "AudL007", columnDefinition = "TEXT", nullable = false)
    private String previousValues; // Valores anteriores en JSON

    @Column(name = "AudL008", columnDefinition = "TEXT", nullable = false)
    private String newValues; // Valores nuevos en JSON

    @Column(name = "AudL009", length = 50, nullable = false)
    private String ipAddress; // Dirección IP o Identificador del terminal

    @Column(name = "AudL010", nullable = false)
    private Integer windowId; // ID de la ventana/módulo desde donde se originó

    // Constructor vacío
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor con parámetros principales
    public AuditLog(Integer employeeCode, String action, String tableName, String recordId,
            String previousValues, String newValues, String ipAddress, Integer windowId) {
        this();
        this.employeeCode = employeeCode;
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.previousValues = previousValues;
        this.newValues = newValues;
        this.ipAddress = ipAddress;
        this.windowId = windowId;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(Integer employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getPreviousValues() {
        return previousValues;
    }

    public void setPreviousValues(String previousValues) {
        this.previousValues = previousValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getWindowId() {
        return windowId;
    }

    public void setWindowId(Integer windowId) {
        this.windowId = windowId;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", employeeCode=" + employeeCode +
                ", action='" + action + '\'' +
                ", tableName='" + tableName + '\'' +
                ", recordId='" + recordId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", windowId=" + windowId +
                '}';
    }
}
