package com.example.backend_jovyweb.config.sp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Stored Procedures del módulo de Impresoras.
 * 
 * Agrupa los SPs de la gestión de impresoras
 *
 */
@Configuration
@ConfigurationProperties(prefix = "sp.printer")
public class PrinterSpProperties {
    private String database;
    private String createSp;
    private String updateSp;
    private String deleteSp;
    private String deleteDefinitivoSp;
    private String getActiveSp;
    private String getByIdSp;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getCreateSp() {
        return createSp;
    }

    public void setCreateSp(String createSp) {
        this.createSp = createSp;
    }

    public String getUpdateSp() {
        return updateSp;
    }

    public void setUpdateSp(String updateSp) {
        this.updateSp = updateSp;
    }

    public String getDeleteSp() {
        return deleteSp;
    }

    public void setDeleteSp(String deleteSp) {
        this.deleteSp = deleteSp;
    }

    public String getDeleteDefinitivoSp() {
        return deleteDefinitivoSp;
    }

    public void setDeleteDefinitivoSp(String deleteDefinitivoSp) {
        this.deleteDefinitivoSp = deleteDefinitivoSp;
    }

    public String getGetActiveSp() {
        return getActiveSp;
    }

    public void setGetActiveSp(String getActiveSp) {
        this.getActiveSp = getActiveSp;
    }

    public String getGetByIdSp() {
        return getByIdSp;
    }

    public void setGetByIdSp(String getByIdSp) {
        this.getByIdSp = getByIdSp;
    }

    /**
     * Construye la consulta EXEC para cualquier SP de impresoras.
     * 
     * @param spName     nombre del SP
     * @param parameters parámetros de la query (puede ser vacío)
     * @return String con la consulta EXEC completa
     */
    public String buildExecQuery(String spName, String parameters) {
        String query = String.format("EXEC [%s].[dbo].[%s]", this.database, spName);
        return (parameters != null && !parameters.isEmpty()) ? query + " " + parameters : query;
    }

    /**
     * Construye la consulta EXEC base sin parámetros.
     * 
     * @param spName nombre del SP
     * @return String con la consulta EXEC base
     */
    public String buildExecBase(String spName) {
        return String.format("EXEC [%s].[dbo].[%s]", this.database, spName);
    }
}
