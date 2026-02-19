package com.example.backend_jovyweb.modules.produccion.repository.impl;

import com.example.backend_jovyweb.modules.produccion.dto.AreaMaquinaDTO;
import com.example.backend_jovyweb.modules.produccion.dto.EmpleadosActivosDTO;
import com.example.backend_jovyweb.modules.produccion.dto.OrdenProductividadDTO;
import com.example.backend_jovyweb.modules.produccion.dto.ProduccionDataDTO;
import com.example.backend_jovyweb.modules.produccion.repository.ProduccionDataRepository;
import com.example.backend_jovyweb.config.StoredProceduresProperties;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

/**
 * Implementación del repositorio para datos consolidados de producción.
 * Ejecuta el SP PROD_GDataPAPMPRPER y transforma los resultados en DTOs.
 */
@Repository
public class ProduccionDataRepositoryImpl implements ProduccionDataRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final StoredProceduresProperties spProps;

    public ProduccionDataRepositoryImpl(JdbcTemplate jdbcTemplate, StoredProceduresProperties spProps) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
        this.spProps = spProps;
    }

    @Override
    public ProduccionDataDTO obtenerDatosProduccion(Integer proR011, Integer perD015) {
        try {
            // Construir la consulta EXEC usando el método helper de
            // StoredProceduresProperties
            String sql = spProps.getProduccionData().buildExecQuery("@ProR011 = ?, @PerD015 = ?");

            String jsonResponse = jdbcTemplate.queryForObject(
                    sql,
                    new Object[] {
                            proR011 != null ? proR011 : 0,
                            perD015 != null ? perD015 : 1
                    },
                    String.class);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                return crearProduccionDataVacia();
            }

            // Limpiar caracteres de control del JSON
            jsonResponse = limpiarCaracteresDeControl(jsonResponse);

            // Parsear el JSON recibido
            return parsearProduccionData(jsonResponse);

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener datos del SP PROD_GDataPAPMPRPER", e);
        }
    }

    @Override
    public ProduccionDataDTO obtenerDatosProduccionSinFiltro() {
        return obtenerDatosProduccion(null, null);
    }

    /**
     * Parsea el JSON del SP en la estructura de ProduccionDataDTO.
     * El JSON tiene la forma: [{ "PROD_ARMACH": [...], "PROD_ACTEMP": [...],
     * "PROD_OP": [...] }]
     */
    private ProduccionDataDTO parsearProduccionData(String jsonResponse) throws Exception {
        // El SP devuelve un array con un objeto dentro: [{ ... }]
        // Necesitamos extraer ese objeto
        List<Map<String, Object>> listaMapas = objectMapper.readValue(
                jsonResponse,
                new TypeReference<List<Map<String, Object>>>() {
                });

        if (listaMapas.isEmpty()) {
            return crearProduccionDataVacia();
        }

        Map<String, Object> datosMap = listaMapas.get(0);
        ProduccionDataDTO produccionData = new ProduccionDataDTO();

        // Procesar PROD_ARMACH
        if (datosMap.containsKey("PROD_ARMACH")) {
            Object areaObj = datosMap.get("PROD_ARMACH");
            List<AreaMaquinaDTO> areas = mapearAreasMaxinas(areaObj);
            produccionData.setAreasMachines(areas);
        }

        // Procesar PROD_ACTEMP
        if (datosMap.containsKey("PROD_ACTEMP")) {
            Object empObj = datosMap.get("PROD_ACTEMP");
            List<EmpleadosActivosDTO> empleados = mapearEmpleadosActivos(empObj);
            produccionData.setActiveEmployees(empleados);
        }

        // Procesar PROD_OP
        if (datosMap.containsKey("PROD_OP")) {
            Object ordObj = datosMap.get("PROD_OP");
            List<OrdenProductividadDTO> ordenes = mapearOrdenesProductividad(ordObj);
            produccionData.setOpenOrders(ordenes);
        }

        return produccionData;
    }

    /**
     * Mapea el array de áreas y máquinas desde el JSON.
     */
    private List<AreaMaquinaDTO> mapearAreasMaxinas(Object areaObj) throws Exception {
        if (areaObj == null) {
            return List.of();
        }

        List<Map<String, Object>> listaMaps = objectMapper.convertValue(
                areaObj,
                new TypeReference<List<Map<String, Object>>>() {
                });

        return listaMaps.stream()
                .map(map -> {
                    AreaMaquinaDTO dto = new AreaMaquinaDTO();
                    dto.setProA001(stringToInt(map.get("ProA001")));
                    dto.setProA002((String) map.get("ProA002"));
                    dto.setProM001((String) map.get("ProM001"));
                    dto.setProM002((String) map.get("ProM002"));
                    return dto;
                })
                .toList();
    }

    /**
     * Mapea el array de empleados activos desde el JSON.
     */
    private List<EmpleadosActivosDTO> mapearEmpleadosActivos(Object empObj) throws Exception {
        if (empObj == null) {
            return List.of();
        }

        List<Map<String, Object>> listaMaps = objectMapper.convertValue(
                empObj,
                new TypeReference<List<Map<String, Object>>>() {
                });

        return listaMaps.stream()
                .map(map -> {
                    EmpleadosActivosDTO dto = new EmpleadosActivosDTO();
                    dto.setPerD001(stringToInt(map.get("PerD001")));
                    dto.setName((String) map.get("Name"));
                    return dto;
                })
                .toList();
    }

    /**
     * Mapea el array de órdenes de productividad desde el JSON.
     */
    private List<OrdenProductividadDTO> mapearOrdenesProductividad(Object ordObj) throws Exception {
        if (ordObj == null) {
            return List.of();
        }

        List<Map<String, Object>> listaMaps = objectMapper.convertValue(
                ordObj,
                new TypeReference<List<Map<String, Object>>>() {
                });

        return listaMaps.stream()
                .map(this::mapearOrdenProductividad)
                .toList();
    }

    /**
     * Mapea un mapa individual a OrdenProductividadDTO.
     */
    private OrdenProductividadDTO mapearOrdenProductividad(Map<String, Object> map) {
        OrdenProductividadDTO dto = new OrdenProductividadDTO();
        dto.setProR001(stringToInt(map.get("ProR001")));
        dto.setProR002((String) map.get("ProR002"));
        dto.setProR003((String) map.get("ProR003"));
        dto.setProR004((String) map.get("ProR004"));
        dto.setProR005((String) map.get("ProR005"));
        dto.setProR006((String) map.get("ProR006"));
        dto.setProR007((String) map.get("ProR007"));
        dto.setProR008((String) map.get("ProR008"));
        dto.setProR009((String) map.get("ProR009"));
        dto.setProR010((String) map.get("ProR010"));
        dto.setProA001((String) map.get("ProA001"));
        dto.setProM001((String) map.get("ProM001"));
        dto.setProR011((String) map.get("ProR011"));
        dto.setProR012((String) map.get("ProR012"));
        dto.setProR013((String) map.get("ProR013"));
        dto.setProR014((String) map.get("ProR014"));
        dto.setProR015((String) map.get("ProR015"));
        dto.setProR016((String) map.get("ProR016"));
        dto.setProR017((String) map.get("ProR017"));
        dto.setProR018((String) map.get("ProR018"));
        dto.setProR019((String) map.get("ProR019"));
        return dto;
    }

    /**
     * Crea un DTO de producción vacío con listas vacías.
     */
    private ProduccionDataDTO crearProduccionDataVacia() {
        return new ProduccionDataDTO(List.of(), List.of(), List.of());
    }

    /**
     * Convierte un objeto a int de forma segura.
     */
    private Integer stringToInt(Object value) {
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
     * Limpia caracteres de control del JSON.
     */
    private String limpiarCaracteresDeControl(String json) {
        if (json == null) {
            return json;
        }
        return json
                .replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("[\\u0000-\\u0008\\u000B-\\u000C\\u000E-\\u001F]", "");
    }
}
