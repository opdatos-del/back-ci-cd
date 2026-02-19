package com.example.backend_jovyweb.modules.auth.dto;

/**
 * DTO para respuesta de renovación de JWT.
 * Retorna el nuevo JWT y opcionalmente el refresh token (para rotación).
 */
public class RefreshResponse {
    private String token;
    private String refreshToken;
    private String message;
    private boolean success;

    public RefreshResponse() {
    }

    public RefreshResponse(String token, String refreshToken, String message, boolean success) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.message = message;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

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
        return "RefreshResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
