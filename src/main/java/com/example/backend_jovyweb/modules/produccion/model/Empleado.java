package com.example.backend_jovyweb.modules.produccion.model;

/**
 * Modelo (Entidad) que representa un empleado en la base de datos.
 * Contiene toda la información personal y laboral del empleado.
 * Las propiedades mapean directamente con las columnas de la tabla de
 * empleados.
 */
public class Empleado {

    private int PerD001; // ID del empleado
    private String PerD002N; // Nombre del empleado
    private String PerD003; // Campo adicional
    private String PerD004; // Campo adicional
    private String AreD002; // Área del empleado
    private String PueD002; // Puesto del empleado
    private String PerD005; // RFC del empleado
    private String PerD045; // CURP del empleado
    private String PerD006; // NSS del empleado
    private String PerD016; // Calle del domicilio
    private String PerD017; // Colonia del domicilio
    private String PerD019; // Municipio del domicilio
    private String PerD018; // Código postal del domicilio
    private String PerD021; // Email del empleado
    private java.util.Date PerD037; // Fecha de nacimiento
    private int PerD012Y; // Años de antigüedad
    private int PerD012M; // Meses de antigüedad
    private java.util.Date PerD012E; // Fecha de entrada
    private String PerD234; // Teléfono del empleado

    /**
     * Obtiene el ID del empleado.
     * 
     * @return ID del empleado
     */
    public int getPerD001() {
        return PerD001;
    }

    /**
     * Establece el ID del empleado.
     * 
     * @param PerD001 ID a establecer
     */
    public void setPerD001(int PerD001) {
        this.PerD001 = PerD001;
    }

    /**
     * Obtiene el nombre del empleado.
     * 
     * @return Nombre del empleado
     */
    public String getPerD002N() {
        return PerD002N;
    }

    /**
     * Establece el nombre del empleado.
     * 
     * @param PerD002N Nombre a establecer
     */
    public void setPerD002N(String PerD002N) {
        this.PerD002N = PerD002N;
    }

    /**
     * Obtiene PerD003.
     * 
     * @return PerD003
     */
    public String getPerD003() {
        return PerD003;
    }

    /**
     * Establece PerD003.
     * 
     * @param PerD003 Valor a establecer
     */
    public void setPerD003(String PerD003) {
        this.PerD003 = PerD003;
    }

    /**
     * Obtiene PerD004.
     * 
     * @return PerD004
     */
    public String getPerD004() {
        return PerD004;
    }

    public void setPerD004(String PerD004) {
        this.PerD004 = PerD004;
    }

    public String getAreD002() {
        return AreD002;
    }

    public void setAreD002(String AreD002) {
        this.AreD002 = AreD002;
    }

    public String getPueD002() {
        return PueD002;
    }

    public void setPueD002(String PueD002) {
        this.PueD002 = PueD002;
    }

    public String getPerD005() {
        return PerD005;
    }

    public void setPerD005(String PerD005) {
        this.PerD005 = PerD005;
    }

    public String getPerD045() {
        return PerD045;
    }

    public void setPerD045(String PerD045) {
        this.PerD045 = PerD045;
    }

    public String getPerD006() {
        return PerD006;
    }

    public void setPerD006(String PerD006) {
        this.PerD006 = PerD006;
    }

    public String getPerD016() {
        return PerD016;
    }

    public void setPerD016(String PerD016) {
        this.PerD016 = PerD016;
    }

    public String getPerD017() {
        return PerD017;
    }

    public void setPerD017(String PerD017) {
        this.PerD017 = PerD017;
    }

    public String getPerD019() {
        return PerD019;
    }

    public void setPerD019(String PerD019) {
        this.PerD019 = PerD019;
    }

    public String getPerD018() {
        return PerD018;
    }

    public void setPerD018(String PerD018) {
        this.PerD018 = PerD018;
    }

    public String getPerD021() {
        return PerD021;
    }

    public void setPerD021(String PerD021) {
        this.PerD021 = PerD021;
    }

    public java.util.Date getPerD037() {
        return PerD037;
    }

    public void setPerD037(java.util.Date PerD037) {
        this.PerD037 = PerD037;
    }

    public int getPerD012Y() {
        return PerD012Y;
    }

    public void setPerD012Y(int PerD012Y) {
        this.PerD012Y = PerD012Y;
    }

    public int getPerD012M() {
        return PerD012M;
    }

    public void setPerD012M(int PerD012M) {
        this.PerD012M = PerD012M;
    }

    public java.util.Date getPerD012E() {
        return PerD012E;
    }

    public void setPerD012E(java.util.Date PerD012E) {
        this.PerD012E = PerD012E;
    }

    public String getPerD234() {
        return PerD234;
    }

    public void setPerD234(String PerD234) {
        this.PerD234 = PerD234;
    }

}
