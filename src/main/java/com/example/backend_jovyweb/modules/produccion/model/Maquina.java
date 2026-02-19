
package com.example.backend_jovyweb.modules.produccion.model;

/**
 * Modelo que representa una máquina de producción.
 * Contiene identificador, nombre y referencia al área.
 */
public class Maquina {
    private int proM001; // ID único de la máquina
    private String proM002; // Nombre o descripción de la máquina
    private int proA001; // ID del área a la que pertenece

    /**
     * Constructor por defecto.
     */
    public Maquina() {
    }

    /**
     * Constructor con parámetros.
     * 
     * @param proM001 ID de la máquina
     * @param proM002 Nombre o descripción
     * @param proA001 ID del área
     */
    public Maquina(int proM001, String proM002, int proA001) {
        this.proM001 = proM001;
        this.proM002 = proM002;
        this.proA001 = proA001;
    }

    /**
     * Obtiene el ID de la máquina.
     * 
     * @return ID de la máquina
     */
    public int getProM001() {
        return proM001;
    }

    /**
     * Establece el ID de la máquina.
     * 
     * @param proM001 ID de la máquina
     */
    public void setProM001(int proM001) {
        this.proM001 = proM001;
    }

    /**
     * Obtiene el nombre o descripción de la máquina.
     * 
     * @return nombre o descripción
     */
    public String getProM002() {
        return proM002;
    }

    /**
     * Establece el nombre o descripción de la máquina.
     * 
     * @param proM002 nombre o descripción
     */
    public void setProM002(String proM002) {
        this.proM002 = proM002;
    }

    /**
     * Obtiene el ID del área a la que pertenece la máquina.
     * 
     * @return ID del área
     */
    public int getProA001() {
        return proA001;
    }

    /**
     * Establece el ID del área a la que pertenece la máquina.
     * 
     * @param proA001 ID del área
     */
    public void setProA001(int proA001) {
        this.proA001 = proA001;
    }
}
