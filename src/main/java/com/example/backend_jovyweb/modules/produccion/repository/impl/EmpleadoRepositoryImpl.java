package com.example.backend_jovyweb.modules.produccion.repository.impl;

import com.example.backend_jovyweb.modules.produccion.model.Empleado;
import com.example.backend_jovyweb.modules.produccion.dto.EmpleadoDTO;
import com.example.backend_jovyweb.modules.produccion.repository.EmpleadoRepository;
import com.example.backend_jovyweb.config.StoredProceduresProperties;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private StoredProceduresProperties spProps;

    public EmpleadoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Empleado> obtenerEmpleados() {
        try {
            // Construir la consulta usando la configuración centralizada
            String sql = spProps.getEmpleados().buildExecQuery(
                "@toDay = NULL, @idEmployed = NULL, @PerD015 = NULL, @Fecha_ini = NULL, @Fecha_fin = NULL"
            );

            String jsonResponse = jdbcTemplate.queryForObject(sql, String.class);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                return List.of();
            }

            // Limpiar caracteres de control del JSON antes de parsearlo
            jsonResponse = limpiarCaracteresDeControl(jsonResponse);

            // Parsear JSON recibido desde el SP
            List<Map<String, Object>> listaMaps = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            // Mapear JSON a objetos Empleado
            return listaMaps.stream().map(this::mapEmpleadoFromJson).toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar respuesta JSON del SP MASTER_GPDK_1_DEV", e);
        }
    }

    /**
     * Mapea un objeto JSON del SP a una entidad Empleado
     * 
     * Para agregar más campos:
     * 1. Hacer que el SP incluya el campo en el JSON (MASTER_GPDK_1_DEV)
     * 2. Agregar el mapeo aquí (ej: empleado.setAreD002((String)
     * jsonMap.get("AreD002"));)
     * 3. Agregar el campo y getter/setter en Empleado o EmpleadoDTO
     */
    private Empleado mapEmpleadoFromJson(Map<String, Object> jsonMap) {
        Empleado empleado = new Empleado();
        empleado.setPerD001(stringToInt(jsonMap.get("PerD001")));
        empleado.setPerD002N((String) jsonMap.get("PerD002N"));
        empleado.setPueD002((String) jsonMap.get("PueD002"));
        return empleado;
    }

    @Override
    public Optional<EmpleadoDTO> obtenerEmpleadoPorCodigo(int codigoEmpleado) {
        try {
            // Construir la consulta usando la configuración centralizada
            String sql = spProps.getEmpleados().buildExecQuery(
                "@toDay = NULL, @idEmployed = ?, @PerD015 = NULL, @Fecha_ini = NULL, @Fecha_fin = NULL"
            );

            String jsonResponse = jdbcTemplate.queryForObject(
                    sql,
                    new Object[] { String.valueOf(codigoEmpleado) },
                    String.class);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                return Optional.empty();
            }

            // Limpiar caracteres de control del JSON antes de parsearlo
            jsonResponse = limpiarCaracteresDeControl(jsonResponse);

            // Parsear JSON recibido desde el SP
            List<Map<String, Object>> listaMaps = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            if (listaMaps.isEmpty()) {
                return Optional.empty();
            }

            // Mapear el primer resultado a EmpleadoDTO
            Map<String, Object> empleadoMap = listaMaps.get(0);
            EmpleadoDTO dto = new EmpleadoDTO();
            dto.setIdEmpleado(stringToInt(empleadoMap.get("PerD001")));
            dto.setNombre((String) empleadoMap.get("PerD002N"));
            dto.setPuesto((String) empleadoMap.get("PueD002"));

            return Optional.of(dto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener empleado por código usando SP MASTER_GPDK_1_DEV", e);
        }
    }

    /**
     * Convierte un objeto (String o Number) a int de forma segura.
     * 
     * @param value objeto que puede ser String, Number o null
     * @return valor como int, o 0 si el valor es null o inválido
     */
    private int stringToInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Limpia caracteres de control del JSON (saltos de línea, carriage return,
     * etc.)
     * que no están siendo escapados correctamente en la generación del JSON en SQL
     * Server.
     * 
     * @param json string con posibles caracteres de control
     * @return json limpiado
     */
    private String limpiarCaracteresDeControl(String json) {
        if (json == null) {
            return json;
        }
        // Reemplazar caracteres de control comunes
        return json
                .replaceAll("[\\r\\n\\t]", " ") // Reemplaza CR, LF, TAB por espacio
                .replaceAll("[\\u0000-\\u0008\\u000B-\\u000C\\u000E-\\u001F]", ""); // Reemplaza otros caracteres de control
    }
}
