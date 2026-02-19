package com.example.backend_jovyweb.modules.auth.dto;

/**
 * DTO para respuesta de login.
 * Retorna los datos del usuario autenticado sin exponer información sensible.
 * 
 * Nomenclatura: VLOG = Validación Login
 * RefreshToken se envía solo en cookie HttpOnly, no en el body.
 */
public class LoginResponse {
    private Integer VLOG_EMPLOYEE_CODE;
    private String VLOG_NAME;
    private String VLOG_EMAIL;
    private Integer VLOG_DEPARTMENT_CODE;
    private Integer VLOG_ACCESS_NUMBER;
    private String VLOG_SLP_CODE;
    private String VLOG_MESSAGE;
    private boolean VLOG_SUCCESS;

    // Constructor vacío
    public LoginResponse() {
    }

    // Constructor completo
    public LoginResponse(Integer VLOG_EMPLOYEE_CODE, String VLOG_NAME, String VLOG_EMAIL,
            Integer VLOG_DEPARTMENT_CODE, Integer VLOG_ACCESS_NUMBER,
            String VLOG_SLP_CODE, String VLOG_MESSAGE, boolean VLOG_SUCCESS) {
        this.VLOG_EMPLOYEE_CODE = VLOG_EMPLOYEE_CODE;
        this.VLOG_NAME = VLOG_NAME;
        this.VLOG_EMAIL = VLOG_EMAIL;
        this.VLOG_DEPARTMENT_CODE = VLOG_DEPARTMENT_CODE;
        this.VLOG_ACCESS_NUMBER = VLOG_ACCESS_NUMBER;
        this.VLOG_SLP_CODE = VLOG_SLP_CODE;
        this.VLOG_MESSAGE = VLOG_MESSAGE;
        this.VLOG_SUCCESS = VLOG_SUCCESS;
    }

    // Getters y Setters
    public Integer getVLOG_EMPLOYEE_CODE() {
        return VLOG_EMPLOYEE_CODE;
    }

    public void setVLOG_EMPLOYEE_CODE(Integer VLOG_EMPLOYEE_CODE) {
        this.VLOG_EMPLOYEE_CODE = VLOG_EMPLOYEE_CODE;
    }

    public String getVLOG_NAME() {
        return VLOG_NAME;
    }

    public void setVLOG_NAME(String VLOG_NAME) {
        this.VLOG_NAME = VLOG_NAME;
    }

    public String getVLOG_EMAIL() {
        return VLOG_EMAIL;
    }

    public void setVLOG_EMAIL(String VLOG_EMAIL) {
        this.VLOG_EMAIL = VLOG_EMAIL;
    }

    public Integer getVLOG_DEPARTMENT_CODE() {
        return VLOG_DEPARTMENT_CODE;
    }

    public void setVLOG_DEPARTMENT_CODE(Integer VLOG_DEPARTMENT_CODE) {
        this.VLOG_DEPARTMENT_CODE = VLOG_DEPARTMENT_CODE;
    }

    public Integer getVLOG_ACCESS_NUMBER() {
        return VLOG_ACCESS_NUMBER;
    }

    public void setVLOG_ACCESS_NUMBER(Integer VLOG_ACCESS_NUMBER) {
        this.VLOG_ACCESS_NUMBER = VLOG_ACCESS_NUMBER;
    }

    public String getVLOG_SLP_CODE() {
        return VLOG_SLP_CODE;
    }

    public void setVLOG_SLP_CODE(String VLOG_SLP_CODE) {
        this.VLOG_SLP_CODE = VLOG_SLP_CODE;
    }

    public String getVLOG_MESSAGE() {
        return VLOG_MESSAGE;
    }

    public void setVLOG_MESSAGE(String VLOG_MESSAGE) {
        this.VLOG_MESSAGE = VLOG_MESSAGE;
    }

    public boolean isVLOG_SUCCESS() {
        return VLOG_SUCCESS;
    }

    public void setVLOG_SUCCESS(boolean VLOG_SUCCESS) {
        this.VLOG_SUCCESS = VLOG_SUCCESS;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "VLOG_EMPLOYEE_CODE=" + VLOG_EMPLOYEE_CODE +
                ", VLOG_NAME='" + VLOG_NAME + '\'' +
                ", VLOG_DEPARTMENT_CODE=" + VLOG_DEPARTMENT_CODE +
                ", VLOG_SUCCESS=" + VLOG_SUCCESS +
                '}';
    }
}
