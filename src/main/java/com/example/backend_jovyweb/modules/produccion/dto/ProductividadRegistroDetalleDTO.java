package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO detallado para registros de productividad.
 * Utilizado en vistas de detalle para mostrar información completa de una
 * orden,
 * incluyendo métricas calculadas como minutos perdidos/ganados y productividad.
 * <p>
 * Este DTO contiene todos los datos necesarios para mostrar un historial
 * detallado de productividad de una orden específica.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductividadRegistroDetalleDTO {

    @JsonProperty("PROD_idReg")
    private int registroId;

    @PositiveOrZero(message = "El número de orden debe ser mayor o igual a 0")
    @JsonProperty("PROD_ordNum")
    private int numeroOrden;

    @JsonProperty("PROD_prodDesc")
    private String productoDescripcion;

    @JsonProperty("PROD_fIni")
    private LocalDate fecha;

    @JsonProperty("PROD_hIni")
    private LocalTime hora;

    @JsonProperty("PROD_pctRec")
    private double productividad;

    @JsonProperty("PROD_minPerd")
    private double minutosPerdidos;

    private double minutosGanados;

    @JsonProperty("PROD_supCod")
    private int supervisorId;

    @JsonProperty("PROD_supNom")
    private String supervisorNombre;

    @JsonProperty("PROD_kgCofre")
    private double kgProcesados;

    @JsonProperty("PROD_maqVel")
    private int velocidad;

    @JsonProperty("PROD_prodTeo")
    private int produccionTeorica;

    @JsonProperty("PROD_prodReal")
    private int produccionReal;

    @JsonProperty("PROD_idArea")
    private int areaId;

    @JsonProperty("PROD_areaNom")
    private String areaNombre;

    @JsonProperty("PROD_idMaq")
    private int maquinaId;

    @JsonProperty("PROD_maqNom")
    private String maquinaNombre;

    @JsonProperty("PROD_loteNum")
    private String numeroLote;

    @JsonProperty("PROD_cantPlan")
    private String cantidadPlanificada;

    @JsonProperty("PROD_obs")
    private String observaciones;

    /**
     * Constructor por defecto.
     */
    public ProductividadRegistroDetalleDTO() {
    }

    /**
     * Constructor parametrizado con todos los campos.
     */
    public ProductividadRegistroDetalleDTO(int registroId, int numeroOrden, String productoDescripcion,
            LocalDate fecha, LocalTime hora, double productividad,
            double minutosPerdidos, double minutosGanados,
            int supervisorId, String supervisorNombre,
            double kgProcesados, int velocidad,
            int produccionTeorica, int produccionReal,
            int areaId, String areaNombre, int maquinaId,
            String maquinaNombre, String numeroLote,
            String cantidadPlanificada, String observaciones) {
        this.registroId = registroId;
        this.numeroOrden = numeroOrden;
        this.productoDescripcion = productoDescripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.productividad = productividad;
        this.minutosPerdidos = minutosPerdidos;
        this.minutosGanados = minutosGanados;
        this.supervisorId = supervisorId;
        this.supervisorNombre = supervisorNombre;
        this.kgProcesados = kgProcesados;
        this.velocidad = velocidad;
        this.produccionTeorica = produccionTeorica;
        this.produccionReal = produccionReal;
        this.areaId = areaId;
        this.areaNombre = areaNombre;
        this.maquinaId = maquinaId;
        this.maquinaNombre = maquinaNombre;
        this.numeroLote = numeroLote;
        this.cantidadPlanificada = cantidadPlanificada;
        this.observaciones = observaciones;
    }

    // ========== Getters y Setters ==========

    public int getRegistroId() {
        return registroId;
    }

    public void setRegistroId(int registroId) {
        this.registroId = registroId;
    }

    /**
     * Obtiene el número de orden de producción.
     * 
     * @return Número de orden (Ej: M220251204)
     */
    public int getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(int numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    /**
     * Obtiene la descripción del producto fabricado.
     * 
     * @return Descripción (Ej: Rings durazno)
     */
    public String getProductoDescripcion() {
        return productoDescripcion;
    }

    public void setProductoDescripcion(String productoDescripcion) {
        this.productoDescripcion = productoDescripcion;
    }

    /**
     * Obtiene la fecha del registro de productividad.
     * 
     * @return Fecha en formato LocalDate
     */
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene la hora del registro de productividad.
     * 
     * @return Hora en formato LocalTime
     */
    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    /**
     * Obtiene el porcentaje de productividad logrado.
     * Fórmula: (produccionReal / produccionTeorica) * 100
     * 
     * @return Porcentaje (0-100, Ej: 62)
     */
    public double getProductividad() {
        return productividad;
    }

    public void setProductividad(double productividad) {
        this.productividad = productividad;
    }

    /**
     * Obtiene los minutos perdidos en la producción.
     * Minutos donde se perdió productividad respecto a la teórica.
     * 
     * @return Minutos perdidos (Ej: 22.66)
     */
    public double getMinutosPerdidos() {
        return minutosPerdidos;
    }

    public void setMinutosPerdidos(double minutosPerdidos) {
        this.minutosPerdidos = minutosPerdidos;
    }

    /**
     * Obtiene los minutos ganados en la producción.
     * Minutos donde se superó la productividad teórica (si aplica).
     * 
     * @return Minutos ganados
     */
    public double getMinutosGanados() {
        return minutosGanados;
    }

    public void setMinutosGanados(double minutosGanados) {
        this.minutosGanados = minutosGanados;
    }

    /**
     * Obtiene el ID del supervisor responsable.
     * 
     * @return ID del supervisor
     */
    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    /**
     * Obtiene el nombre completo del supervisor responsable.
     * 
     * @return Nombre del supervisor (Ej: MONICA ELIZABETH SANCHEZ FLORES)
     */
    public String getSupervisorNombre() {
        return supervisorNombre;
    }

    public void setSupervisorNombre(String supervisorNombre) {
        this.supervisorNombre = supervisorNombre;
    }

    /**
     * Obtiene la cantidad de kilogramos procesados.
     * 
     * @return Kg procesados (Ej: 1.60)
     */
    public double getKgProcesados() {
        return kgProcesados;
    }

    public void setKgProcesados(double kgProcesados) {
        this.kgProcesados = kgProcesados;
    }

    /**
     * Obtiene la velocidad de la máquina en unidades/minuto.
     * 
     * @return Velocidad (Ej: 24)
     */
    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    /**
     * Obtiene la producción teórica esperada.
     * 
     * @return Unidades teóricas (Ej: 1920)
     */
    public int getProduccionTeorica() {
        return produccionTeorica;
    }

    public void setProduccionTeorica(int produccionTeorica) {
        this.produccionTeorica = produccionTeorica;
    }

    /**
     * Obtiene la producción real alcanzada.
     * 
     * @return Unidades reales (Ej: 1195)
     */
    public int getProduccionReal() {
        return produccionReal;
    }

    public void setProduccionReal(int produccionReal) {
        this.produccionReal = produccionReal;
    }

    /**
     * Obtiene el ID del área donde se realizó la producción.
     * 
     * @return ID del área
     */
    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    /**
     * Obtiene el nombre del área donde se realizó la producción.
     * 
     * @return Nombre del área
     */
    public String getAreaNombre() {
        return areaNombre;
    }

    public void setAreaNombre(String areaNombre) {
        this.areaNombre = areaNombre;
    }

    /**
     * Obtiene el ID de la máquina utilizada.
     * 
     * @return ID de la máquina
     */
    public int getMaquinaId() {
        return maquinaId;
    }

    public void setMaquinaId(int maquinaId) {
        this.maquinaId = maquinaId;
    }

    /**
     * Obtiene el nombre/código de la máquina utilizada.
     * 
     * @return Nombre de la máquina
     */
    public String getMaquinaNombre() {
        return maquinaNombre;
    }

    public void setMaquinaNombre(String maquinaNombre) {
        this.maquinaNombre = maquinaNombre;
    }

    /**
     * Obtiene el número de lote de producción.
     * 
     * @return Número de lote
     */
    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    /**
     * Obtiene la cantidad planificada para esta orden.
     * 
     * @return Cantidad planificada
     */
    public String getCantidadPlanificada() {
        return cantidadPlanificada;
    }

    public void setCantidadPlanificada(String cantidadPlanificada) {
        this.cantidadPlanificada = cantidadPlanificada;
    }

    /**
     * Obtiene observaciones adicionales del registro de productividad.
     * 
     * @return Observaciones o comentarios
     */
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
