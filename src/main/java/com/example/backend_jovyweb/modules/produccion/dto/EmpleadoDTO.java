package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO para el empleado.
 * Transporta datos de empleados entre las capas de la aplicación.
 * Las propiedades siguen la nomenclatura de la base de datos con anotaciones
 * JSON.
 * 
 * ¿CÓMO AGREGAR MÁS CAMPOS?
 * Si necesitamos mostrar más información (como área, email, departamento,
 * etc.):
 * 1. Agregar el campo privado con prefijo GDPK_ (ej: private String
 * GDPK_areaEmpleado;)
 * 2. Generar su respectivo getter y setter
 * 3. Actualizar el VIEW en schema.sql para incluir esos campos en el JSON
 * 4. Modificar EmpleadoRepositoryImpl.mapEmpleadoFromJson() para mapear los
 * nuevos campos
 * 5. Swagger se actualizará automáticamente con los nuevos campos
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpleadoDTO {
    /** ID del empleado (GDPK_idEmp) */
    @JsonProperty("GDPK_idEmp")
    private int GDPK_idEmp;

    /** Nombre del empleado (GDPK_nomEmp) */
    @JsonProperty("GDPK_nomEmp")
    private String GDPK_nomEmp;

    /** Puesto del empleado (GDPK_pstEmp) */
    @JsonProperty("GDPK_pstEmp")
    private String GDPK_pstEmp;

    /**
     * Obtiene el ID del empleado.
     * 
     * @return ID del empleado
     */
    @JsonIgnore
    public int getIdEmpleado() {
        return GDPK_idEmp;
    }

    /**
     * Establece el ID del empleado.
     * 
     * @param GDPK_idEmp ID a establecer
     */
    public void setIdEmpleado(int GDPK_idEmp) {
        this.GDPK_idEmp = GDPK_idEmp;
    }

    /**
     * Obtiene el nombre del empleado.
     * 
     * @return Nombre del empleado
     */
    @JsonIgnore
    public String getNombre() {
        return GDPK_nomEmp;
    }

    /**
     * Establece el nombre del empleado.
     * 
     * @param GDPK_nomEmp Nombre a establecer
     */
    public void setNombre(String GDPK_nomEmp) {
        this.GDPK_nomEmp = GDPK_nomEmp;
    }

    /**
     * Obtiene el puesto del empleado.
     * 
     * @return Puesto del empleado
     */
    @JsonIgnore
    public String getPuesto() {
        return GDPK_pstEmp;
    }

    /**
     * Establece el puesto del empleado.
     * 
     * @param GDPK_pstEmp Puesto a establecer
     */
    public void setPuesto(String GDPK_pstEmp) {
        this.GDPK_pstEmp = GDPK_pstEmp;
    }
}
