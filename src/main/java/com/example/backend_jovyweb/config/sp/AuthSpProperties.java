package com.example.backend_jovyweb.config.sp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Stored Procedures del módulo de Autenticación.
 * 
 * Agrupa los SPs del login y validación de token.
 */
@Configuration
@ConfigurationProperties(prefix = "sp.auth")
public class AuthSpProperties {
    private String database;
    private String loginSp;
    private String validateTokenSp;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getLoginSp() {
        return loginSp;
    }

    public void setLoginSp(String loginSp) {
        this.loginSp = loginSp;
    }

    public String getValidateTokenSp() {
        return validateTokenSp;
    }

    public void setValidateTokenSp(String validateTokenSp) {
        this.validateTokenSp = validateTokenSp;
    }

    /**
     * Construye la consulta EXEC para cualquier SP de autenticación.
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
