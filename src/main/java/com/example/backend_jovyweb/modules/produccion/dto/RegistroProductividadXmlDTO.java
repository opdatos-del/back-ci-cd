package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * DTO para recibir registros de productividad en formato XML desde el cliente.
 * Mapea los campos del XML directamente a los nombres de columnas de la BD.
 * Todos los campos son String ya que en XML los atributos siempre son strings.
 * La conversión a tipos específicos se realiza en el servicio.
 * 
 * Estructura XML esperada:
 * <root>
 * <row>
 * <proR002>2025</proR002>
 * <proR003>2026-02-10</proR003>
 * <proR004>08:30:00</proR004>
 * ...
 * </row>
 * </root>
 */
@JacksonXmlRootElement(localName = "RegistroProductividadXmlDTO")
public class RegistroProductividadXmlDTO {

    @JacksonXmlProperty(localName = "row")
    private RowData row;

    public RowData getRow() {
        return row;
    }

    public void setRow(RowData row) {
        this.row = row;
    }

    /**
     * Clase interna que representa el elemento row con sus elementos anidados
     */
    @JacksonXmlRootElement(localName = "row")
    public static class RowData {
        @JacksonXmlProperty(localName = "proR002")
        private String proR002;

        @JacksonXmlProperty(localName = "proR003")
        private String proR003;

        @JacksonXmlProperty(localName = "proR004")
        private String proR004;

        @JacksonXmlProperty(localName = "proR005")
        private String proR005;

        @JacksonXmlProperty(localName = "proR006")
        private String proR006;

        @JacksonXmlProperty(localName = "proR007")
        private String proR007;

        @JacksonXmlProperty(localName = "proR008")
        private String proR008;

        @JacksonXmlProperty(localName = "proR009")
        private String proR009;

        @JacksonXmlProperty(localName = "proR010")
        private String proR010;

        @JacksonXmlProperty(localName = "proA001")
        private String proA001;

        @JacksonXmlProperty(localName = "proM001")
        private String proM001;

        @JacksonXmlProperty(localName = "proR011")
        private String proR011;

        @JacksonXmlProperty(localName = "proR012")
        private String proR012;

        @JacksonXmlProperty(localName = "proR013")
        private String proR013;

        @JacksonXmlProperty(localName = "proR014")
        private String proR014;

        @JacksonXmlProperty(localName = "proR015")
        private String proR015;

        @JacksonXmlProperty(localName = "proR016")
        private String proR016;

        @JacksonXmlProperty(localName = "proR017")
        private String proR017;

        @JacksonXmlProperty(localName = "proR018")
        private String proR018;

        @JacksonXmlProperty(localName = "proR019")
        private String proR019;

        // Getters y Setters
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
    }
}
