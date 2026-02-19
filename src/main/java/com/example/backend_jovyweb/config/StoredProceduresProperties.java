package com.example.backend_jovyweb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades de configuración para Stored Procedures (Legacy).
 * 
 * NOTA: Las configuraciones de módulos específicos (Productivity, Printer,
 * Auth)
 * han sido movidas a archivos separados en el paquete config.sp.
 * 
 * Este archivo mantiene solo configuraciones generales y módulos legacy
 * que aún no han sido refactorizados (empleados, produccionData).
 * 
 * Ejemplo en application.properties:
 * sps.main-database=JovyDataADW_DEV
 * sps.empleados.name=MASTER_GPDK_1_DEV
 */
@Configuration
@ConfigurationProperties(prefix = "sps")
public class StoredProceduresProperties {
    private String mainDatabase; // BD principal
    private String secondDatabase; // BD secundaria (para autenticación, etc)
    private SpConfig empleados = new SpConfig();
    private ProduccionDataSPConfig produccionData = new ProduccionDataSPConfig();

    public String getMainDatabase() {
        return mainDatabase;
    }

    public void setMainDatabase(String mainDatabase) {
        this.mainDatabase = mainDatabase;
    }

    public String getSecondDatabase() {
        return secondDatabase;
    }

    public void setSecondDatabase(String secondDatabase) {
        this.secondDatabase = secondDatabase;
    }

    public SpConfig getEmpleados() {
        return empleados;
    }

    public void setEmpleados(SpConfig empleados) {
        this.empleados = empleados;
    }

    public ProduccionDataSPConfig getProduccionData() {
        return produccionData;
    }

    public void setProduccionData(ProduccionDataSPConfig produccionData) {
        this.produccionData = produccionData;
    }

    /**
     * Configuración de un SP específico (simple).
     * Solo contiene el nombre del SP. La BD se obtiene de mainDatabase.
     */
    public class SpConfig {
        private String database;
        private String name;

        public String getDatabase() {
            // Si este SP tiene BD específica, usar esa; sino usar mainDatabase
            return (database != null && !database.isEmpty()) ? database : mainDatabase;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * Construye la consulta EXEC completa: EXEC [database].[dbo].[spName]
         * 
         * @return String con la consulta base lista para EXEC
         */
        public String buildExecBase() {
            return String.format("EXEC [%s].[dbo].[%s]", this.getDatabase(), this.name);
        }

        /**
         * Construye la consulta EXEC completa con parámetros.
         * 
         * @param parameters parámetros EXEC (ejemplo: "@param1 = NULL, @param2 = ?")
         * @return String con la consulta EXEC completa
         */
        public String buildExecQuery(String parameters) {
            return buildExecBase() + " " + parameters;
        }
    }

    /**
     * Configuración de SPs de datos de producción.
     * Agrupa los SPs: PROD_GDataPAPMPRPER
     */
    public class ProduccionDataSPConfig {
        private String database;
        private String getDataSp;

        public String getDatabase() {
            return (database != null && !database.isEmpty()) ? database : mainDatabase;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getGetDataSp() {
            return getDataSp;
        }

        public void setGetDataSp(String getDataSp) {
            this.getDataSp = getDataSp;
        }

        /**
         * Construye la consulta EXEC para el SP de datos de producción.
         * 
         * @param parameters parámetros de la query
         * @return String con la consulta EXEC completa
         */
        public String buildExecQuery(String parameters) {
            String query = String.format("EXEC [%s].[dbo].[%s]", this.getDatabase(), this.getDataSp);
            return (parameters != null && !parameters.isEmpty()) ? query + " " + parameters : query;
        }
    }
}
