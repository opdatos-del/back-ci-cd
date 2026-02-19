package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO para la máquina de producción.
 * Transporta datos de máquinas entre las capas de la aplicación.
 * Incluye información del ID, nombre y área a la que pertenece.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaquinaDTO {
    /** ID de la máquina (PROD_idMaq) */
    @JsonProperty("PROD_idMaq")
    private int PROD_idMaq;

    /** Nombre de la máquina (PROD_nomMaq) */
    @JsonProperty("PROD_nomMaq")
    private String PROD_nomMaq;

    /** ID del área a la que pertenece (PROD_idArea) */
    @JsonProperty("PROD_idArea")
    private int PROD_idArea;

    /**
     * Constructor sin argumentos.
     */
    public MaquinaDTO() {
    }

    /**
     * Constructor con todos los argumentos.
     * 
     * @param PROD_idMaq  ID de la máquina
     * @param PROD_nomMaq Nombre de la máquina
     * @param PROD_idArea ID del área
     */
    public MaquinaDTO(int PROD_idMaq, String PROD_nomMaq, int PROD_idArea) {
        this.PROD_idMaq = PROD_idMaq;
        this.PROD_nomMaq = PROD_nomMaq;
        this.PROD_idArea = PROD_idArea;
    }

    /**
     * Obtiene el ID de la máquina.
     * 
     * @return ID de la máquina
     */
    @JsonIgnore
    public int getId() {
        return PROD_idMaq;
    }

    /**
     * Establece el ID de la máquina.
     * 
     * @param PROD_idMaq ID a establecer
     */
    public void setId(int PROD_idMaq) {
        this.PROD_idMaq = PROD_idMaq;
    }

    /**
     * Obtiene el nombre de la máquina.
     * 
     * @return Nombre de la máquina
     */
    @JsonIgnore
    public String getNombre() {
        return PROD_nomMaq;
    }

    /**
     * Establece el nombre de la máquina.
     * 
     * @param PROD_nomMaq Nombre a establecer
     */
    public void setNombre(String PROD_nomMaq) {
        this.PROD_nomMaq = PROD_nomMaq;
    }

    /**
     * Obtiene el ID del área de la máquina.
     * 
     * @return ID del área
     */
    @JsonIgnore
    public int getAreaId() {
        return PROD_idArea;
    }

    /**
     * Establece el ID del área de la máquina.
     * 
     * @param PROD_idArea ID del área a establecer
     */
    public void setAreaId(int PROD_idArea) {
        this.PROD_idArea = PROD_idArea;
    }
}
