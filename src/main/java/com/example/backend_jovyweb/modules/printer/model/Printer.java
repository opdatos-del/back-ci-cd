package com.example.backend_jovyweb.modules.printer.model;

import java.time.LocalDateTime;

/**
 * Entidad que representa una impresora en el sistema.
 * Mapea la tabla MASTER_PRINTER de SQL Server.
 */
public class Printer {

    private int PRI001; // ID de la impresora
    private String PRI002; // Nombre de la impresora
    private String PRI003; // IP de la impresora
    private String PRI004; // Ubicación de la impresora
    private String PRI005; // Tipo de impresora
    private String PRI006; // Descripción de la impresora
    private LocalDateTime PRI007; // Fecha de registro
    private int PRI008; // Estado de la impresora (activo/inactivo)
    private int PRI009; // Puerto de la impresora (por defecto 9100)

    // Getters y Setters
    public int getId() {
        return PRI001;
    }

    public void setId(int PRI001) {
        this.PRI001 = PRI001;
    }

    public String getNombre() {
        return PRI002;
    }

    public void setNombre(String PRI002) {
        this.PRI002 = PRI002;
    }

    public String getIp() {
        return PRI003;
    }

    public void setIp(String PRI003) {
        this.PRI003 = PRI003;
    }

    public String getUbicacion() {
        return PRI004;
    }

    public void setUbicacion(String PRI004) {
        this.PRI004 = PRI004;
    }

    public String getTipo() {
        return PRI005;
    }

    public void setTipo(String PRI005) {
        this.PRI005 = PRI005;
    }

    public String getDescripcion() {
        return PRI006;
    }

    public void setDescripcion(String PRI006) {
        this.PRI006 = PRI006;
    }

    public LocalDateTime getFechaRegistro() {
        return PRI007;
    }

    public void setFechaRegistro(LocalDateTime PRI007) {
        this.PRI007 = PRI007;
    }

    public int getEstado() {
        return PRI008;
    }

    public void setEstado(int PRI008) {
        this.PRI008 = PRI008;
    }

    public int getPuerto() {
        return PRI009;
    }

    public void setPuerto(int PRI009) {
        this.PRI009 = PRI009;
    }
}
