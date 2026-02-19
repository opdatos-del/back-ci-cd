package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO para el área de producción.
 * Transporta datos de áreas entre las capas de la aplicación.
 * Las propiedades siguen la nomenclatura de la base de datos con anotaciones
 * JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaDTO {
    @JsonProperty("PROD_idArea")
    private int PROD_idArea;
    @JsonProperty("PROD_nomArea")
    private String PROD_nomArea;

    /**
     * Constructor sin argumentos.
     */
    public AreaDTO() {
    }

    /**
     * Constructor con todos los argumentos.
     * 
     * @param PROD_idArea  ID del área
     * @param PROD_nomArea Nombre del área
     */
    public AreaDTO(int PROD_idArea, String PROD_nomArea) {
        this.PROD_idArea = PROD_idArea;
        this.PROD_nomArea = PROD_nomArea;
    }

    /**
     * Obtiene el ID del área.
     * 
     * @return ID del área
     */
    @JsonIgnore
    public int getId() {
        return PROD_idArea;
    }

    /**
     * Establece el ID del área.
     * 
     * @param PROD_idArea ID a establecer
     */
    public void setId(int PROD_idArea) {
        this.PROD_idArea = PROD_idArea;
    }

    /**
     * Obtiene el nombre del área.
     * 
     * @return Nombre del área
     */
    @JsonIgnore
    public String getNombre() {
        return PROD_nomArea;
    }

    /**
     * Establece el nombre del área.
     * 
     * @param PROD_nomArea Nombre a establecer
     */
    public void setNombre(String PROD_nomArea) {
        this.PROD_nomArea = PROD_nomArea;
    }
}
