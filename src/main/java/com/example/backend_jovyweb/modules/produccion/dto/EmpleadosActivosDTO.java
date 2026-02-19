package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar Empleados Activos.
 * Transporta datos del SP PROD_GDataPAPMPRPER.
 * Contiene información básica del empleado: ID y nombre completo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpleadosActivosDTO {

    /** ID del empleado (PerD001) */
    @JsonProperty("PerD001")
    private Integer perD001;

    /** Nombre completo del empleado (PerD002 + PerD003 + PerD004) */
    @JsonProperty("Name")
    private String name;

    public EmpleadosActivosDTO() {
    }

    public EmpleadosActivosDTO(Integer perD001, String name) {
        this.perD001 = perD001;
        this.name = name;
    }

    public Integer getPerD001() {
        return perD001;
    }

    public void setPerD001(Integer perD001) {
        this.perD001 = perD001;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EmpleadosActivosDTO{" +
                "perD001=" + perD001 +
                ", name='" + name + '\'' +
                '}';
    }
}
