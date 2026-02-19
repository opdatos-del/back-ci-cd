package com.example.backend_jovyweb.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para solicitud de login.
 * Contiene las credenciales del usuario.
 * 
 * Nomenclatura: VLOG = Validación Login
 * 
 * El cliente debe enviar:
 * {
 * "VLOG_USERNAME": "usuario",
 * "VLOG_PASSWORD": "contraseña"
 * }
 */
public class LoginRequest {

    @JsonProperty("VLOG_USERNAME")
    private String vlogUsername;

    @JsonProperty("VLOG_PASSWORD")
    private String vlogPassword;

    // Constructor vacío
    public LoginRequest() {
    }

    // Constructor completo
    public LoginRequest(String vlogUsername, String vlogPassword) {
        this.vlogUsername = vlogUsername;
        this.vlogPassword = vlogPassword;
    }

    // Getters y Setters
    public String getVlogUsername() {
        return vlogUsername;
    }

    public void setVlogUsername(String vlogUsername) {
        this.vlogUsername = vlogUsername;
    }

    public String getVlogPassword() {
        return vlogPassword;
    }

    public void setVlogPassword(String vlogPassword) {
        this.vlogPassword = vlogPassword;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "vlogUsername='" + vlogUsername + '\'' +
                '}';
    }
}
