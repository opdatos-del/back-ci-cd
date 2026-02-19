package com.example.backend_jovyweb.modules.printer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * DTO para transportar datos de impresora entre capas.
 */
public class PrinterDTO {
    /** ID de la impresora. Identificador único. */
    @JsonProperty("PRI_IdImp")
    private Integer id;
    /** Nombre de la impresora. Campo obligatorio. */
    @JsonProperty("PRI_Nombre")
    private String nombre;
    /** IP de la impresora. Campo obligatorio para conectividad. */
    @JsonProperty("PRI_Ip")
    private String ip;
    /** Ubicación de la impresora. Indica dónde está instalada. */
    @JsonProperty("PRI_Ubicacion")
    private String ubicacion;
    /** Tipo de impresora. Por ejemplo: láser, inyección, etc. */
    @JsonProperty("PRI_Tipo")
    private String tipo;
    /** Descripción de la impresora. Información adicional. */
    @JsonProperty("PRI_Descripcion")
    private String descripcion;
    /**
     * Fecha de registro de la impresora. Marca de cuándo fue registrada.
     */
    @JsonProperty("PRI_FechaReg")
    private LocalDateTime fechaRegistro;
    /** Estado de la impresora. 1 = Activo, 0 = Inactivo. */
    @JsonProperty("PRI_Estado")
    private Integer estado;
    /** Puerto de la impresora. Por defecto 9100. */
    @JsonProperty("PRI_Puerto")
    private Integer puerto;

    // Getters y Setters
    /**
     * Obtiene el identificador único de la impresora.
     * 
     * @return ID de la impresora
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador único de la impresora.
     * 
     * @param id ID a establecer
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la impresora.
     * 
     * @return Nombre de la impresora
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la impresora.
     * 
     * @param nombre Nombre de la impresora
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la dirección IP de la impresora.
     * 
     * @return IP de la impresora
     */
    public String getIp() {
        return ip;
    }

    /**
     * Establece la dirección IP de la impresora.
     * 
     * @param ip IP de la impresora
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Obtiene la ubicación de la impresora.
     * 
     * @return Ubicación de la impresora
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Establece la ubicación de la impresora.
     * 
     * @param ubicacion Ubicación de la impresora
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * Obtiene el tipo de impresora.
     * 
     * @return Tipo de impresora
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de impresora.
     * 
     * @param tipo Tipo de impresora
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la descripción de la impresora.
     * 
     * @return Descripción de la impresora
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la impresora.
     * 
     * @param descripcion Descripción de la impresora
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la fecha de registro de la impresora.
     * 
     * @return Fecha de registro
     */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de registro de la impresora.
     * 
     * @param fechaRegistro Fecha de registro
     */
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Obtiene el estado de la impresora.
     * 
     * @return Estado: 1 = Activo, 0 = Inactivo
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la impresora.
     * 
     * @param estado Estado: 1 = Activo, 0 = Inactivo
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el puerto de la impresora.
     * 
     * @return Puerto de la impresora (por defecto 9100)
     */
    public Integer getPuerto() {
        return puerto;
    }

    /**
     * Establece el puerto de la impresora.
     * 
     * @param puerto Puerto a establecer
     */
    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }
}
