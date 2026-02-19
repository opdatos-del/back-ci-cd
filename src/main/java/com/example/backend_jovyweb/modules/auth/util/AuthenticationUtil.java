package com.example.backend_jovyweb.modules.auth.util;

import com.example.backend_jovyweb.config.sp.AuthSpProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para autenticación de usuarios.
 * Comunica con los Stored Procedures de la base de datos para validar
 * credenciales
 * y gestionar tokens de sesión.
 * 
 * Stored Procedures:
 * - MASTER_VLogin: Valida credenciales y genera token
 * - MASTER_VToken: Valida token y marca sesión como activa
 */
@Component
public class AuthenticationUtil {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SimpleJdbcCall loginCall;
    private final SimpleJdbcCall validateTokenCall;

    public AuthenticationUtil(DataSource dataSource, AuthSpProperties spProps) {
        // AuthSpProperties inyectado pero no almacenado ya que
        // solo se usa en el constructor para inicializar los SPs

        // Obtener los nombres de los SPs desde la configuración centralizada
        String loginSpName = spProps.getLoginSp();
        String validateTokenSpName = spProps.getValidateTokenSp();

        // Inicializar el SP de Login
        this.loginCall = new SimpleJdbcCall(dataSource)
                .withProcedureName(loginSpName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("Username", Types.NVARCHAR),
                        new SqlParameter("Password", Types.NVARCHAR));

        // Inicializar el SP de Validación de Token
        this.validateTokenCall = new SimpleJdbcCall(dataSource)
                .withProcedureName(validateTokenSpName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("Token", Types.NVARCHAR),
                        new SqlParameter("Username", Types.NVARCHAR));
    }

    /**
     * Autentica al usuario con credenciales y devuelve el token generado.
     * Ejecuta MASTER_VLogin SP que valida credenciales y genera token en BD.
     *
     * @param username nombre de usuario
     * @param password contraseña
     * @return mapa con los datos del usuario incluyendo token
     * @throws RuntimeException si hay error en la autenticación
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> authenticate(String username, String password) {
        try {
            logger.info("▶ Iniciando autenticación para usuario: {}", username);

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("Username", username)
                    .addValue("Password", password);

            logger.debug("Parámetros enviados al SP: Username={}, Password={}", username, "***");

            Map<String, Object> result = loginCall.execute(params);

            logger.debug("Respuesta bruta del SP MASTER_VLogin: {}", result);
            logger.debug("Claves en resultado: {}", result.keySet());

            // El SP retorna en #result-set-1 que contiene un List de Maps
            String jsonOutput = null;

            if (result.containsKey("#result-set-1")) {
                List<?> resultSet = (List<?>) result.get("#result-set-1");
                if (resultSet != null && !resultSet.isEmpty()) {
                    Map<String, Object> firstRow = (Map<String, Object>) resultSet.get(0);
                    Object jsonVLoginObj = firstRow.get("jsonVLogin");

                    if (jsonVLoginObj instanceof List) {
                        List<?> jsonVLoginList = (List<?>) jsonVLoginObj;
                        if (!jsonVLoginList.isEmpty()) {
                            jsonOutput = (String) jsonVLoginList.get(0);
                        }
                    } else {
                        jsonOutput = (String) jsonVLoginObj;
                    }
                }
            }

            if (jsonOutput == null) {
                logger.error("⚠ No se pudo extraer JSON del resultado del SP");
                logger.debug("Estructura completa del resultado:");
                result.forEach((k, v) -> logger.debug("  {} = {}", k, v));
                throw new RuntimeException("SP no retornó datos válidos");
            }

            logger.info("JSON retornado por MASTER_VLogin:\n{}", jsonOutput);

            // Parsear el JSON retornado
            Map<String, Object> response = parseJsonResponse(jsonOutput);
            logger.debug("Respuesta parseada: {} (claves: {})", response, response.keySet());

            // Mostrar todos los campos
            response.forEach((k, v) -> logger.debug("  Campo: {} = {}", k, v));

            // Log para ver el token, el employeeCode y todo el objeto
            logger.info("Datos completos retornados por el login: {}", response);
            if (response.containsKey("Token")) {
                logger.info("Token retornado: {}", response.get("Token"));
            }
            if (response.containsKey("EmployeeCode")) {
                logger.info("EmployeeCode retornado: {}", response.get("EmployeeCode"));
            }

            if (isAuthenticationSuccessful(response)) {
                logger.info("✓ Autenticación exitosa para usuario: {}", username);
                return response;
            } else {
                String errorMsg = (String) response.get("Message");
                logger.warn("✗ Autenticación fallida para usuario: {} - Status: {} - Mensaje: {}",
                        username, response.get("Status"), errorMsg);
                throw new RuntimeException("Credenciales inválidas: " + errorMsg);
            }

        } catch (Exception e) {
            logger.error("ERROR en autenticación para usuario: {} - {}", username, e.getMessage(), e);
            throw new RuntimeException("Error al autenticar: " + e.getMessage(), e);
        }
    }

    /**
     * Valida un token existente en la BD y marca la sesión como activa.
     * Ejecuta MASTER_VToken SP que verifica token y actualiza Session = 1.
     *
     * @param token    token a validar
     * @param username nombre de usuario
     * @return respuesta del SP con status
     * @throws RuntimeException si hay error
     */
    public Map<String, Object> validateTokenInDb(String token, String username) {
        try {
            logger.debug("Validando token en BD para usuario: {}", username);

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("Token", token)
                    .addValue("Username", username);

            Map<String, Object> result = validateTokenCall.execute(params);

            logger.debug("Respuesta bruta del SP MASTER_VToken: {}", result);

            String jsonOutput = (String) result.get("jsonVToken");
            logger.info("JSON retornado por MASTER_VToken:\n{}", jsonOutput);

            Map<String, Object> response = parseJsonResponse(jsonOutput);
            logger.debug("Respuesta parseada: {}", response);

            return response;

        } catch (Exception e) {
            logger.error("ERROR al validar token en BD", e);
            throw new RuntimeException("Error al validar token: " + e.getMessage(), e);
        }
    }

    /**
     * Valida si el usuario tiene permiso para acceder.
     * Verifica si Status = 1 (éxito) en la respuesta.
     *
     * @param response respuesta del SP
     * @return true si es exitosa, false si no
     */
    public boolean hasAccessPermission(Map<String, Object> response) {
        Object status = response.get("Status");
        if (status == null) {
            return false;
        }
        return "1".equals(status.toString());
    }

    /**
     * Extrae el token retornado por el SP de login.
     * Busca el campo "Token" que retorna el SP.
     *
     * @param response respuesta del SP
     * @return token del SP
     */
    public String extractToken(Map<String, Object> response) {
        return (String) response.get("Token");
    }

    /**
     * Extrae el código de empleado de la respuesta.
     * Busca el campo "EmployeeID" que retorna el SP.
     *
     * @param response respuesta del SP
     * @return código de empleado
     */
    public Integer extractEmployeeCode(Map<String, Object> response) {
        Object code = response.get("EmployeeID");
        if (code == null || code.toString().isEmpty()) {
            logger.warn("⚠ EmployeeID vacío en respuesta del SP");
            return null;
        }
        try {
            return Integer.parseInt(code.toString());
        } catch (NumberFormatException e) {
            logger.warn("⚠ EmployeeID no es un número válido: {}", code);
            return null;
        }
    }

    /**
     * Extrae el código de departamento de la respuesta.
     * Busca el campo "Department" que retorna el SP.
     *
     * @param response respuesta del SP
     * @return código de departamento
     */
    public Integer extractDepartmentCode(Map<String, Object> response) {
        Object code = response.get("Department");
        if (code == null) {
            return null;
        }
        try {
            return Integer.parseInt(code.toString());
        } catch (NumberFormatException e) {
            logger.warn("⚠ Department no es un número válido: {}", code);
            return null;
        }
    }

    /**
     * Parsea el JSON retornado por los Stored Procedures.
     * Los SPs retornan JSON en el formato: [{"Status":"1", "Message":"...", ...}]
     *
     * @param jsonOutput JSON string del SP
     * @return mapa con los datos parseados
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonResponse(String jsonOutput) {
        try {
            if (jsonOutput == null || jsonOutput.trim().isEmpty()) {
                logger.warn("JSON output vacío del SP");
                return new HashMap<>();
            }

            // Remover los corchetes del array y parsear el primer objeto
            String cleanJson = jsonOutput;
            if (cleanJson.startsWith("[")) {
                cleanJson = cleanJson.substring(1, cleanJson.length() - 1);
            }

            logger.debug("JSON limpio a parsear: {}", cleanJson);
            Map<String, Object> parsed = (Map<String, Object>) objectMapper.readValue(cleanJson, Map.class);
            logger.debug("JSON parseado correctamente. Claves: {}", parsed.keySet());

            return parsed;
        } catch (Exception e) {
            logger.error("ERROR parseando JSON del SP: {} - Error: {}", jsonOutput, e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Verifica si la autenticación fue exitosa.
     * El SP retorna Status = 1 cuando es exitoso.
     *
     * @param response respuesta parseada del SP
     * @return true si Status = 1
     */
    private boolean isAuthenticationSuccessful(Map<String, Object> response) {
        Object status = response.get("Status");
        boolean success = "1".equals(status != null ? status.toString() : "");
        logger.debug("Status: {} -> Éxito: {}", status, success);
        return success;
    }
}
