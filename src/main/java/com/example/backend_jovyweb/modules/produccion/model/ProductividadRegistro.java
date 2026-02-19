
package com.example.backend_jovyweb.modules.produccion.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modelo que representa un registro de productividad en producción.
 * Incluye información de orden, supervisor, producto, área, máquina y métricas.
 */
public class ProductividadRegistro {
    private int proR001; // ID del registro
    private int proR002; // Número de orden
    private LocalDate proR003; // Fecha de inicio
    private LocalTime proR004; // Hora de inicio
    private int proR005; // Código del supervisor
    private String proR006; // Nombre del supervisor
    private String proR007; // Cantidad planificada
    private int proR008; // Código del producto
    private String proR009; // Descripción del producto
    private String proR010; // Número de lote
    private int proA001; // ID del área
    private int proM001; // ID de la máquina
    private int proR011; // Estado del registro
    private int proR012; // Kg por cofre
    private int proR013; // Velocidad de máquina
    private int proR014; // Producción teórica
    private int proR015; // Producción real
    private int proR016; // Porcentaje recibido
    private String proR017; // Observaciones
    private int proR018; // Minutos perdidos
    private int proR019; // Campo adicional (especificar uso)

    /**
     * Constructor por defecto.
     */
    public ProductividadRegistro() {
    }

    // Getters y Setters

    /**
     * Obtiene el ID del registro.
     * 
     * @return ID del registro
     */
    public int getProR001() {
        return proR001;
    }

    /**
     * Establece el ID del registro.
     * 
     * @param proR001 ID del registro
     */
    public void setProR001(int proR001) {
        this.proR001 = proR001;
    }

    /**
     * Obtiene el número de orden.
     * 
     * @return número de orden
     */
    public int getProR002() {
        return proR002;
    }

    /**
     * Establece el número de orden.
     * 
     * @param proR002 número de orden
     */
    public void setProR002(int proR002) {
        this.proR002 = proR002;
    }

    /**
     * Obtiene la fecha de inicio.
     * 
     * @return fecha de inicio
     */
    public LocalDate getProR003() {
        return proR003;
    }

    /**
     * Establece la fecha de inicio.
     * 
     * @param proR003 fecha de inicio
     */
    public void setProR003(LocalDate proR003) {
        this.proR003 = proR003;
    }

    /**
     * Obtiene la hora de inicio.
     * 
     * @return hora de inicio
     */
    public LocalTime getProR004() {
        return proR004;
    }

    /**
     * Establece la hora de inicio.
     * 
     * @param proR004 hora de inicio
     */
    public void setProR004(LocalTime proR004) {
        this.proR004 = proR004;
    }

    /**
     * Obtiene el código del supervisor.
     * 
     * @return código del supervisor
     */
    public int getProR005() {
        return proR005;
    }

    /**
     * Establece el código del supervisor.
     * 
     * @param proR005 código del supervisor
     */
    public void setProR005(int proR005) {
        this.proR005 = proR005;
    }

    /**
     * Obtiene el nombre del supervisor.
     * 
     * @return nombre del supervisor
     */
    public String getProR006() {
        return proR006;
    }

    /**
     * Establece el nombre del supervisor.
     * 
     * @param proR006 nombre del supervisor
     */
    public void setProR006(String proR006) {
        this.proR006 = proR006;
    }

    /**
     * Obtiene la cantidad planificada.
     * 
     * @return cantidad planificada
     */
    public String getProR007() {
        return proR007;
    }

    /**
     * Establece la cantidad planificada.
     * 
     * @param proR007 cantidad planificada
     */
    public void setProR007(String proR007) {
        this.proR007 = proR007;
    }

    /**
     * Obtiene el código del producto.
     * 
     * @return código del producto
     */
    public int getProR008() {
        return proR008;
    }

    /**
     * Establece el código del producto.
     * 
     * @param proR008 código del producto
     */
    public void setProR008(int proR008) {
        this.proR008 = proR008;
    }

    /**
     * Obtiene la descripción del producto.
     * 
     * @return descripción del producto
     */
    public String getProR009() {
        return proR009;
    }

    /**
     * Establece la descripción del producto.
     * 
     * @param proR009 descripción del producto
     */
    public void setProR009(String proR009) {
        this.proR009 = proR009;
    }

    /**
     * Obtiene el número de lote.
     * 
     * @return número de lote
     */
    public String getProR010() {
        return proR010;
    }

    /**
     * Establece el número de lote.
     * 
     * @param proR010 número de lote
     */
    public void setProR010(String proR010) {
        this.proR010 = proR010;
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
     * @param proA001 ID del área
     */
    public void setProA001(int proA001) {
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
     * Obtiene el estado del registro.
     * 
     * @return estado
     */
    public int getProR011() {
        return proR011;
    }

    /**
     * Establece el estado del registro.
     * 
     * @param proR011 estado
     */
    public void setProR011(int proR011) {
        this.proR011 = proR011;
    }

    /**
     * Obtiene los kg por cofre.
     * 
     * @return kg por cofre
     */
    public int getProR012() {
        return proR012;
    }

    /**
     * Establece los kg por cofre.
     * 
     * @param proR012 kg por cofre
     */
    public void setProR012(int proR012) {
        this.proR012 = proR012;
    }

    /**
     * Obtiene la velocidad de la máquina.
     * 
     * @return velocidad de la máquina
     */
    public int getProR013() {
        return proR013;
    }

    /**
     * Establece la velocidad de la máquina.
     * 
     * @param proR013 velocidad de la máquina
     */
    public void setProR013(int proR013) {
        this.proR013 = proR013;
    }

    /**
     * Obtiene la producción teórica.
     * 
     * @return producción teórica
     */
    public int getProR014() {
        return proR014;
    }

    /**
     * Establece la producción teórica.
     * 
     * @param proR014 producción teórica
     */
    public void setProR014(int proR014) {
        this.proR014 = proR014;
    }

    /**
     * Obtiene la producción real.
     * 
     * @return producción real
     */
    public int getProR015() {
        return proR015;
    }

    /**
     * Establece la producción real.
     * 
     * @param proR015 producción real
     */
    public void setProR015(int proR015) {
        this.proR015 = proR015;
    }

    /**
     * Obtiene el porcentaje recibido.
     * 
     * @return porcentaje recibido
     */
    public int getProR016() {
        return proR016;
    }

    /**
     * Establece el porcentaje recibido.
     * 
     * @param proR016 porcentaje recibido
     */
    public void setProR016(int proR016) {
        this.proR016 = proR016;
    }

    /**
     * Obtiene las observaciones del registro.
     * 
     * @return observaciones
     */
    public String getProR017() {
        return proR017;
    }

    /**
     * Establece las observaciones del registro.
     * 
     * @param proR017 observaciones
     */
    public void setProR017(String proR017) {
        this.proR017 = proR017;
    }

    /**
     * Obtiene los minutos perdidos.
     * 
     * @return minutos perdidos
     */
    public int getProR018() {
        return proR018;
    }

    /**
     * Establece los minutos perdidos.
     * 
     * @param proR018 minutos perdidos
     */
    public void setProR018(int proR018) {
        this.proR018 = proR018;
    }

    /**
     * Obtiene el valor del campo adicional proR019.
     * 
     * @return valor de proR019
     */
    public int getProR019() {
        return proR019;
    }

    /**
     * Establece el valor del campo adicional proR019.
     * 
     * @param proR019 valor de proR019
     */
    public void setProR019(int proR019) {
        this.proR019 = proR019;
    }
}
