package com.example.backend_jovyweb.modules.auth.dto;

/**
 * DTO para respuesta de logout.
 */
public class LogoutResponse {
    private String message;
    private boolean success;

    // Constructor vacío
    public LogoutResponse() {
    }

    // Constructor completo
    public LogoutResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters y Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
