package com.example.backend_jovyweb.modules.auth.dto;

/**
 * DTO para solicitud de renovación de JWT.
 * El cliente envía el refreshToken para obtener un novo JWT.
 */
public class RefreshRequest {
    private String refreshToken;

    public RefreshRequest() {
    }

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "RefreshRequest{" +
                "refreshToken='"
                + (refreshToken != null ? refreshToken.substring(0, Math.min(20, refreshToken.length())) + "..."
                        : "null")
                + '\'' +
                '}';
    }
}
