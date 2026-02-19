package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar Órdenes de Productividad.
 * Transporta datos del SP PROD_GDataPAPMPRPER.
 * Contiene información detallada de los registros de productividad.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdenProductividadDTO {

    /** ID de la orden (ProR001) */
    @JsonProperty("ProR001")
    private Integer proR001;

    /** ID de la orden secundario (ProR002) */
    @JsonProperty("ProR002")
    private String proR002;

    /** Fecha/Hora (ProR003) */
    @JsonProperty("ProR003")
    private String proR003;

    /** Campo ProR004 */
    @JsonProperty("ProR004")
    private String proR004;

    /** Campo ProR005 */
    @JsonProperty("ProR005")
    private String proR005;

    /** Campo ProR006 */
    @JsonProperty("ProR006")
    private String proR006;

    /** Campo ProR007 */
    @JsonProperty("ProR007")
    private String proR007;

    /** Campo ProR008 */
    @JsonProperty("ProR008")
    private String proR008;

    /** Campo ProR009 */
    @JsonProperty("ProR009")
    private String proR009;

    /** Campo ProR010 */
    @JsonProperty("ProR010")
    private String proR010;

    /** ID del área de producción (ProA001) */
    @JsonProperty("ProA001")
    private String proA001;

    /** ID de la máquina (ProM001) */
    @JsonProperty("ProM001")
    private String proM001;

    /** Estado de la orden (ProR011) */
    @JsonProperty("ProR011")
    private String proR011;

    /** Campo ProR012 */
    @JsonProperty("ProR012")
    private String proR012;

    /** Campo ProR013 */
    @JsonProperty("ProR013")
    private String proR013;

    /** Campo ProR014 */
    @JsonProperty("ProR014")
    private String proR014;

    /** Campo ProR015 */
    @JsonProperty("ProR015")
    private String proR015;

    /** Campo ProR016 */
    @JsonProperty("ProR016")
    private String proR016;

    /** Campo ProR017 */
    @JsonProperty("ProR017")
    private String proR017;

    /** Campo ProR018 */
    @JsonProperty("ProR018")
    private String proR018;

    /** Campo ProR019 */
    @JsonProperty("ProR019")
    private String proR019;

    public OrdenProductividadDTO() {
    }

    public Integer getProR001() {
        return proR001;
    }

    public void setProR001(Integer proR001) {
        this.proR001 = proR001;
    }

    public String getProR002() {
        return proR002;
    }

    public void setProR002(String proR002) {
        this.proR002 = proR002;
    }

    public String getProR003() {
        return proR003;
    }

    public void setProR003(String proR003) {
        this.proR003 = proR003;
    }

    public String getProR004() {
        return proR004;
    }

    public void setProR004(String proR004) {
        this.proR004 = proR004;
    }

    public String getProR005() {
        return proR005;
    }

    public void setProR005(String proR005) {
        this.proR005 = proR005;
    }

    public String getProR006() {
        return proR006;
    }

    public void setProR006(String proR006) {
        this.proR006 = proR006;
    }

    public String getProR007() {
        return proR007;
    }

    public void setProR007(String proR007) {
        this.proR007 = proR007;
    }

    public String getProR008() {
        return proR008;
    }

    public void setProR008(String proR008) {
        this.proR008 = proR008;
    }

    public String getProR009() {
        return proR009;
    }

    public void setProR009(String proR009) {
        this.proR009 = proR009;
    }

    public String getProR010() {
        return proR010;
    }

    public void setProR010(String proR010) {
        this.proR010 = proR010;
    }

    public String getProA001() {
        return proA001;
    }

    public void setProA001(String proA001) {
        this.proA001 = proA001;
    }

    public String getProM001() {
        return proM001;
    }

    public void setProM001(String proM001) {
        this.proM001 = proM001;
    }

    public String getProR011() {
        return proR011;
    }

    public void setProR011(String proR011) {
        this.proR011 = proR011;
    }

    public String getProR012() {
        return proR012;
    }

    public void setProR012(String proR012) {
        this.proR012 = proR012;
    }

    public String getProR013() {
        return proR013;
    }

    public void setProR013(String proR013) {
        this.proR013 = proR013;
    }

    public String getProR014() {
        return proR014;
    }

    public void setProR014(String proR014) {
        this.proR014 = proR014;
    }

    public String getProR015() {
        return proR015;
    }

    public void setProR015(String proR015) {
        this.proR015 = proR015;
    }

    public String getProR016() {
        return proR016;
    }

    public void setProR016(String proR016) {
        this.proR016 = proR016;
    }

    public String getProR017() {
        return proR017;
    }

    public void setProR017(String proR017) {
        this.proR017 = proR017;
    }

    public String getProR018() {
        return proR018;
    }

    public void setProR018(String proR018) {
        this.proR018 = proR018;
    }

    public String getProR019() {
        return proR019;
    }

    public void setProR019(String proR019) {
        this.proR019 = proR019;
    }

    @Override
    public String toString() {
        return "OrdenProductividadDTO{" +
                "proR001=" + proR001 +
                ", proR002='" + proR002 + '\'' +
                ", proR003='" + proR003 + '\'' +
                ", proA001='" + proA001 + '\'' +
                ", proM001='" + proM001 + '\'' +
                ", proR011='" + proR011 + '\'' +
                '}';
    }
}
