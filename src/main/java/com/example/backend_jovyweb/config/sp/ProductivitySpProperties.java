package com.example.backend_jovyweb.config.sp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Stored Procedures del módulo de Productividad.
 * 
 * Agrupa los SPs de producción relacionados con la inserción de registros y
 * obtención de detalles de orden.
 * También incluye configuraciones de tablas relacionadas (máquinas, áreas).
 */
@Configuration
@ConfigurationProperties(prefix = "sp.productivity")
public class ProductivitySpProperties {
    private String database;
    private String insertRecordsSp;
    private String getOrderDetailSp;
    private String tableMachine;
    private String tableArea;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getInsertRecordsSp() {
        return insertRecordsSp;
    }

    public void setInsertRecordsSp(String insertRecordsSp) {
        this.insertRecordsSp = insertRecordsSp;
    }

    public String getGetOrderDetailSp() {
        return getOrderDetailSp;
    }

    public void setGetOrderDetailSp(String getOrderDetailSp) {
        this.getOrderDetailSp = getOrderDetailSp;
    }

    public String getTableMachine() {
        return tableMachine;
    }

    public void setTableMachine(String tableMachine) {
        this.tableMachine = tableMachine;
    }

    public String getTableArea() {
        return tableArea;
    }

    public void setTableArea(String tableArea) {
        this.tableArea = tableArea;
    }

    /**
     * Construye la consulta EXEC completa con parámetros.
     * 
     * @param spName     nombre del SP
     * @param parameters parámetros EXEC (ejemplo: "@xml = ?")
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

    /**
     * Construye una consulta SELECT con la BD calificada.
     * 
     * @param columns     columnas a seleccionar (ejemplo: "ProM001, ProM002,
     *                    ProA001")
     * @param tableName   nombre de la tabla
     * @param whereClause cláusula WHERE completa
     * @return String con la consulta SELECT completa
     */
    public String buildSelectQuery(String columns, String tableName, String whereClause) {
        String query = String.format("SELECT %s FROM [%s].[dbo].[%s]", columns, this.database, tableName);
        return (whereClause != null && !whereClause.isEmpty()) ? query + " " + whereClause : query;
    }
}
