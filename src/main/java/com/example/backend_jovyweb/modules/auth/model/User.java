package com.example.backend_jovyweb.modules.auth.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad ficticia de Usuario.
 * Esta clase NO se persiste en base de datos.
 * Representa los datos retornados por el script PowerShell (.ps1) de
 * autenticación.
 */
public class User {
    private Integer employeeCode;
    private String name;
    private String email;
    private String token;
    private Integer departmentCode; // 1 = administrador/gerente ventas, etc.
    private Integer accessNumber; // 1 = acceso a ventas, si no existe = sin acceso
    private String slpCode;
    private String deviceFingerprint; // SHA256(IP + User-Agent) para validación de dispositivo
    private String refreshToken; // Temporal: Solo para retornar en login, no se persiste
    private LocalDateTime loginTime;
    private boolean active;

    // Constructor vacío
    public User() {
    }

    // Constructor completo
    public User(Integer employeeCode, String name, String email, String token,
            Integer departmentCode, Integer accessNumber, String slpCode) {
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.token = token;
        this.departmentCode = departmentCode;
        this.accessNumber = accessNumber;
        this.slpCode = slpCode;
        this.loginTime = LocalDateTime.now();
        this.active = true;
    }

    // Constructor con deviceFingerprint
    public User(Integer employeeCode, String name, String email, String token,
            Integer departmentCode, Integer accessNumber, String slpCode, String deviceFingerprint) {
        this(employeeCode, name, email, token, departmentCode, accessNumber, slpCode);
        this.deviceFingerprint = deviceFingerprint;
    }

    // Getters y Setters
    public Integer getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(Integer employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(Integer departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Integer getAccessNumber() {
        return accessNumber;
    }

    public void setAccessNumber(Integer accessNumber) {
        this.accessNumber = accessNumber;
    }

    public String getSlpCode() {
        return slpCode;
    }

    public void setSlpCode(String slpCode) {
        this.slpCode = slpCode;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(employeeCode, user.employeeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeCode);
    }

    @Override
    public String toString() {
        return "User{" +
                "employeeCode=" + employeeCode +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", departmentCode=" + departmentCode +
                ", accessNumber=" + accessNumber +
                ", deviceFingerprint='" + deviceFingerprint + '\'' +
                ", active=" + active +
                '}';
    }
}
