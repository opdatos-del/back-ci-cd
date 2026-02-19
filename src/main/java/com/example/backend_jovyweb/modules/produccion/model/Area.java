package com.example.backend_jovyweb.modules.produccion.model;

/**
 * Modelo (Entidad) que representa un área de producción en la base de datos.
 * Mapea las propiedades con los nombres de columnas de la tabla de áreas.
 */
public class Area {
    /** ID del área (PROD_A001) */
    private int proA001;
    /** Nombre del área (PROD_A002) */
    private String proA002;

    /**
     * Constructor sin argumentos.
     */
    public Area() {
    }

    /**
     * Constructor con todos los argumentos.
     * 
     * @param proA001 ID del área
     * @param proA002 Nombre del área
     */
    public Area(int proA001, String proA002) {
        this.proA001 = proA001;
        this.proA002 = proA002;
    }

    /**
     * Obtiene el ID del área.
     * 
     * @return ID del área
     */
    public int getProA001() {
        return proA001;
    }

    /**
     * Establece el ID del área.
     * 
     * @param proA001 ID a establecer
     */
    public void setProA001(int proA001) {
        this.proA001 = proA001;
    }

    /**
     * Obtiene el nombre del área.
     * 
     * @return Nombre del área
     */
    public String getProA002() {
        return proA002;
    }

    /**
     * Establece el nombre del área.
     * 
     * @param proA002 Nombre a establecer
     */
    public void setProA002(String proA002) {
        this.proA002 = proA002;
    }
}
