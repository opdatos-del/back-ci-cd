package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para el registro de productividad en el módulo de producción.
 * Incluye información sobre la orden, supervisor, producto, área, máquina y
 * métricas de producción.
 * <p>
 * Utilizado para la transferencia de datos entre capas y la validación de
 * entradas.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductividadRegistroDTO {
    @JsonProperty("PROD_idReg")
    private int PROD_idReg;
    @NotNull(message = "El número de orden es obligatorio")
    @PositiveOrZero(message = "El número de orden debe ser mayor o igual a 0")
    @JsonProperty("PROD_ordNum")
    private int PROD_ordNum;
    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonProperty("PROD_fIni")
    private LocalDate PROD_fIni;
    @NotNull(message = "La hora de inicio es obligatoria")
    @JsonProperty("PROD_hIni")
    private LocalTime PROD_hIni;
    @NotNull(message = "El código del supervisor es obligatorio")
    @PositiveOrZero(message = "El código del supervisor debe ser mayor o igual a 0")
    @JsonProperty("PROD_supCod")
    private int PROD_supCod;
    @NotBlank(message = "El nombre del supervisor es obligatorio")
    @JsonProperty("PROD_supNom")
    private String PROD_supNom;
    @NotBlank(message = "La cantidad planificada es obligatoria")
    @JsonProperty("PROD_cantPlan")
    private String PROD_cantPlan;
    @NotNull(message = "El código del producto es obligatorio")
    @PositiveOrZero(message = "El código del producto debe ser mayor o igual a 0")
    @JsonProperty("PROD_prodCod")
    private int PROD_prodCod;
    @NotBlank(message = "La descripción del producto es obligatoria")
    @JsonProperty("PROD_prodDesc")
    private String PROD_prodDesc;
    @NotBlank(message = "El número de lote es obligatorio")
    @JsonProperty("PROD_loteNum")
    private String PROD_loteNum;
    @NotNull(message = "El área es obligatoria")
    @PositiveOrZero(message = "El ID del área debe ser mayor o igual a 0")
    @JsonProperty("PROD_idArea")
    private int PROD_idArea;
    @NotNull(message = "La máquina es obligatoria")
    @PositiveOrZero(message = "El ID de la máquina debe ser mayor o igual a 0")
    @JsonProperty("PROD_idMaq")
    private int PROD_idMaq;
    @JsonProperty("PROD_status")
    private int PROD_status;
    @JsonProperty("PROD_kgCofre")
    private int PROD_kgCofre;
    @JsonProperty("PROD_maqVel")
    private int PROD_maqVel;
    @JsonProperty("PROD_prodTeo")
    private int PROD_prodTeo;
    @JsonProperty("PROD_prodReal")
    private int PROD_prodReal;
    @JsonProperty("PROD_pctRec")
    private int PROD_pctRec;
    @JsonProperty("PROD_minPerd")
    private int PROD_minPerd;
    @JsonProperty("PROD_obs")
    private String PROD_obs;

    /**
     * Constructor por defecto.
     */
    public ProductividadRegistroDTO() {
    }

    // Getters y Setters

    /**
     * Obtiene el ID del registro de productividad.
     * 
     * @return ID del registro
     */
    @JsonIgnore
    public int getId() {
        return PROD_idReg;
    }

    /**
     * Establece el ID del registro de productividad.
     * 
     * @param PROD_idReg ID del registro
     */
    public void setId(int PROD_idReg) {
        this.PROD_idReg = PROD_idReg;
    }

    /**
     * Obtiene el número de orden de producción.
     * 
     * @return número de orden
     */
    @JsonIgnore
    public int getNumeroOrden() {
        return PROD_ordNum;
    }

    /**
     * Establece el número de orden de producción.
     * 
     * @param PROD_ordNum número de orden
     */
    public void setNumeroOrden(int PROD_ordNum) {
        this.PROD_ordNum = PROD_ordNum;
    }

    /**
     * Obtiene la fecha de inicio de la producción.
     * 
     * @return fecha de inicio
     */
    @JsonIgnore
    public LocalDate getFechaInicio() {
        return PROD_fIni;
    }

    /**
     * Establece la fecha de inicio de la producción.
     * 
     * @param PROD_fIni fecha de inicio
     */
    public void setFechaInicio(LocalDate PROD_fIni) {
        this.PROD_fIni = PROD_fIni;
    }

    /**
     * Obtiene la hora de inicio de la producción.
     * 
     * @return hora de inicio
     */
    @JsonIgnore
    public LocalTime getHoraInicio() {
        return PROD_hIni;
    }

    /**
     * Establece la hora de inicio de la producción.
     * 
     * @param PROD_hIni hora de inicio
     */
    public void setHoraInicio(LocalTime PROD_hIni) {
        this.PROD_hIni = PROD_hIni;
    }

    /**
     * Obtiene el código del supervisor.
     * 
     * @return código del supervisor
     */
    @JsonIgnore
    public int getCodigoSupervisor() {
        return PROD_supCod;
    }

    /**
     * Establece el código del supervisor.
     * 
     * @param PROD_supCod código del supervisor
     */
    public void setCodigoSupervisor(int PROD_supCod) {
        this.PROD_supCod = PROD_supCod;
    }

    /**
     * Obtiene el nombre del supervisor.
     * 
     * @return nombre del supervisor
     */
    @JsonIgnore
    public String getNombreSupervisor() {
        return PROD_supNom;
    }

    /**
     * Establece el nombre del supervisor.
     * 
     * @param PROD_supNom nombre del supervisor
     */
    public void setNombreSupervisor(String PROD_supNom) {
        this.PROD_supNom = PROD_supNom;
    }

    /**
     * Obtiene la cantidad planificada de producción.
     * 
     * @return cantidad planificada
     */
    @JsonIgnore
    public String getCantidadPlanificada() {
        return PROD_cantPlan;
    }

    /**
     * Establece la cantidad planificada de producción.
     * 
     * @param PROD_cantPlan cantidad planificada
     */
    public void setCantidadPlanificada(String PROD_cantPlan) {
        this.PROD_cantPlan = PROD_cantPlan;
    }

    /**
     * Obtiene el código del producto.
     * 
     * @return código del producto
     */
    @JsonIgnore
    public int getCodigoProducto() {
        return PROD_prodCod;
    }

    /**
     * Establece el código del producto.
     * 
     * @param PROD_prodCod código del producto
     */
    public void setCodigoProducto(int PROD_prodCod) {
        this.PROD_prodCod = PROD_prodCod;
    }

    /**
     * Obtiene la descripción del producto.
     * 
     * @return descripción del producto
     */
    @JsonIgnore
    public String getDescripcionProducto() {
        return PROD_prodDesc;
    }

    /**
     * Establece la descripción del producto.
     * 
     * @param PROD_prodDesc descripción del producto
     */
    public void setDescripcionProducto(String PROD_prodDesc) {
        this.PROD_prodDesc = PROD_prodDesc;
    }

    /**
     * Obtiene el número de lote.
     * 
     * @return número de lote
     */
    @JsonIgnore
    public String getNumeroLote() {
        return PROD_loteNum;
    }

    /**
     * Establece el número de lote.
     * 
     * @param PROD_loteNum número de lote
     */
    public void setNumeroLote(String PROD_loteNum) {
        this.PROD_loteNum = PROD_loteNum;
    }

    /**
     * Obtiene el ID del área de producción.
     * 
     * @return ID del área
     */
    @JsonIgnore
    public int getAreaId() {
        return PROD_idArea;
    }

    /**
     * Establece el ID del área de producción.
     * 
     * @param PROD_idArea ID del área
     */
    public void setAreaId(int PROD_idArea) {
        this.PROD_idArea = PROD_idArea;
    }

    /**
     * Obtiene el ID de la máquina utilizada.
     * 
     * @return ID de la máquina
     */
    @JsonIgnore
    public int getMaquinaId() {
        return PROD_idMaq;
    }

    /**
     * Establece el ID de la máquina utilizada.
     * 
     * @param PROD_idMaq ID de la máquina
     */
    public void setMaquinaId(int PROD_idMaq) {
        this.PROD_idMaq = PROD_idMaq;
    }

    /**
     * Obtiene el estado del registro de productividad.
     * 
     * @return estado
     */
    @JsonIgnore
    public int getStatus() {
        return PROD_status;
    }

    /**
     * Establece el estado del registro de productividad.
     * 
     * @param PROD_status estado
     */
    public void setStatus(int PROD_status) {
        this.PROD_status = PROD_status;
    }

    /**
     * Obtiene los kilogramos por cofre.
     * 
     * @return kg por cofre
     */
    @JsonIgnore
    public int getKgPorCofre() {
        return PROD_kgCofre;
    }

    /**
     * Establece los kilogramos por cofre.
     * 
     * @param PROD_kgCofre kg por cofre
     */
    public void setKgPorCofre(int PROD_kgCofre) {
        this.PROD_kgCofre = PROD_kgCofre;
    }

    /**
     * Obtiene la velocidad de la máquina.
     * 
     * @return velocidad de la máquina
     */
    @JsonIgnore
    public int getVelocidadMaquina() {
        return PROD_maqVel;
    }

    /**
     * Establece la velocidad de la máquina.
     * 
     * @param PROD_maqVel velocidad de la máquina
     */
    public void setVelocidadMaquina(int PROD_maqVel) {
        this.PROD_maqVel = PROD_maqVel;
    }

    /**
     * Obtiene la producción teórica.
     * 
     * @return producción teórica
     */
    @JsonIgnore
    public int getProduccionTeorica() {
        return PROD_prodTeo;
    }

    /**
     * Establece la producción teórica.
     * 
     * @param PROD_prodTeo producción teórica
     */
    public void setProduccionTeorica(int PROD_prodTeo) {
        this.PROD_prodTeo = PROD_prodTeo;
    }

    /**
     * Obtiene la producción real.
     * 
     * @return producción real
     */
    @JsonIgnore
    public int getProduccionReal() {
        return PROD_prodReal;
    }

    /**
     * Establece la producción real.
     * 
     * @param PROD_prodReal producción real
     */
    public void setProduccionReal(int PROD_prodReal) {
        this.PROD_prodReal = PROD_prodReal;
    }

    /**
     * Obtiene el porcentaje recibido.
     * 
     * @return porcentaje recibido
     */
    @JsonIgnore
    public int getPorcentajeRecibido() {
        return PROD_pctRec;
    }

    /**
     * Establece el porcentaje recibido.
     * 
     * @param PROD_pctRec porcentaje recibido
     */
    public void setPorcentajeRecibido(int PROD_pctRec) {
        this.PROD_pctRec = PROD_pctRec;
    }

    /**
     * Obtiene los minutos perdidos.
     * 
     * @return minutos perdidos
     */
    @JsonIgnore
    public int getMinutosPerdidos() {
        return PROD_minPerd;
    }

    /**
     * Establece los minutos perdidos.
     * 
     * @param PROD_minPerd minutos perdidos
     */
    public void setMinutosPerdidos(int PROD_minPerd) {
        this.PROD_minPerd = PROD_minPerd;
    }

    /**
     * Obtiene las observaciones del registro.
     * 
     * @return observaciones
     */
    @JsonIgnore
    public String getObservaciones() {
        return PROD_obs;
    }

    /**
     * Establece las observaciones del registro.
     * 
     * @param PROD_obs observaciones
     */
    public void setObservaciones(String PROD_obs) {
        this.PROD_obs = PROD_obs;
    }
}
