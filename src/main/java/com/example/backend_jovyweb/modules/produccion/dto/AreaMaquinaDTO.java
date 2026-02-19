package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar una Área de Producción con sus Máquinas.
 * Transporta datos del SP PROD_GDataPAPMPRPER.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaMaquinaDTO {

    /** ID del área de producción (ProA001) */
    @JsonProperty("ProA001")
    private Integer proA001;

    /** Nombre del área de producción (ProA002) */
    @JsonProperty("ProA002")
    private String proA002;

    /** ID de la máquina (ProM001) */
    @JsonProperty("ProM001")
    private String proM001;

    /** Nombre de la máquina (ProM002) */
    @JsonProperty("ProM002")
    private String proM002;

    public AreaMaquinaDTO() {
    }

    public AreaMaquinaDTO(Integer proA001, String proA002, String proM001, String proM002) {
        this.proA001 = proA001;
        this.proA002 = proA002;
        this.proM001 = proM001;
        this.proM002 = proM002;
    }

    public Integer getProA001() {
        return proA001;
    }

    public void setProA001(Integer proA001) {
        this.proA001 = proA001;
    }

    public String getProA002() {
        return proA002;
    }

    public void setProA002(String proA002) {
        this.proA002 = proA002;
    }

    public String getProM001() {
        return proM001;
    }

    public void setProM001(String proM001) {
        this.proM001 = proM001;
    }

    public String getProM002() {
        return proM002;
    }

    public void setProM002(String proM002) {
        this.proM002 = proM002;
    }

    @Override
    public String toString() {
        return "AreaMaquinaDTO{" +
                "proA001=" + proA001 +
                ", proA002='" + proA002 + '\'' +
                ", proM001='" + proM001 + '\'' +
                ", proM002='" + proM002 + '\'' +
                '}';
    }
}
