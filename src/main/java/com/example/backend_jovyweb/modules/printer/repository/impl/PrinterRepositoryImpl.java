package com.example.backend_jovyweb.modules.printer.repository.impl;

import com.example.backend_jovyweb.modules.printer.dto.PrinterDTO;
import com.example.backend_jovyweb.modules.printer.repository.PrinterRepository;
import com.example.backend_jovyweb.config.sp.PrinterSpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import java.sql.Types;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementación del repositorio de impresoras.
 */
@Repository
public class PrinterRepositoryImpl implements PrinterRepository {

    private static final Logger logger = LoggerFactory.getLogger(PrinterRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final PrinterSpProperties spProps;

    private SimpleJdbcCall crearImpresoraCall;
    private SimpleJdbcCall actualizarImpresoraCall;
    private SimpleJdbcCall eliminarImpresoraCall;
    private SimpleJdbcCall eliminarImpresoraDefinitivoCall;

    /**
     * Constructor con inyección de JdbcTemplate, DataSource y
     * PrinterSpProperties.
     * 
     * @param jdbcTemplate Template de JDBC para ejecutar queries
     * @param dataSource   DataSource para SimpleJdbcCall
     * @param spProps      Propiedades de configuración de SPs
     */
    public PrinterRepositoryImpl(JdbcTemplate jdbcTemplate, DataSource dataSource, PrinterSpProperties spProps) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.spProps = spProps;
        this.initializeSimpleJdbcCalls();
    }

    /**
     * Inicializa las instancias de SimpleJdbcCall para cada stored procedure.
     * Usa la configuración centralizada de PrinterSpProperties.
     */
    private void initializeSimpleJdbcCalls() {
        // Obtener los nombres de los SPs desde la configuración centralizada
        String createSpName = spProps.getCreateSp();
        String updateSpName = spProps.getUpdateSp();
        String deleteSpName = spProps.getDeleteSp();
        String deleteDefinitivoSpName = spProps.getDeleteDefinitivoSp();

        this.crearImpresoraCall = new SimpleJdbcCall(dataSource)
                .withProcedureName(createSpName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("PRI002", Types.VARCHAR),
                        new SqlParameter("PRI003", Types.VARCHAR),
                        new SqlParameter("PRI004", Types.VARCHAR),
                        new SqlParameter("PRI005", Types.VARCHAR),
                        new SqlParameter("PRI006", Types.VARCHAR),
                        new SqlParameter("PRI007", Types.TIMESTAMP),
                        new SqlParameter("PRI008", Types.INTEGER),
                        new SqlParameter("PRI009", Types.INTEGER));

        this.actualizarImpresoraCall = new SimpleJdbcCall(dataSource)
                .withProcedureName(updateSpName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("PRI001", Types.INTEGER),
                        new SqlParameter("PRI002", Types.VARCHAR),
                        new SqlParameter("PRI003", Types.VARCHAR),
                        new SqlParameter("PRI004", Types.VARCHAR),
                        new SqlParameter("PRI005", Types.VARCHAR),
                        new SqlParameter("PRI006", Types.VARCHAR),
                        new SqlParameter("PRI008", Types.INTEGER),
                        new SqlParameter("PRI009", Types.INTEGER));

        this.eliminarImpresoraCall = new SimpleJdbcCall(dataSource)
                .withProcedureName(deleteSpName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("PRI001", Types.INTEGER));

        this.eliminarImpresoraDefinitivoCall = new SimpleJdbcCall(dataSource)
                .withProcedureName(deleteDefinitivoSpName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("PRI001", Types.INTEGER));
    }

    /**
     * Obtiene la lista de impresoras activas desde la base de datos.
     * 
     * @return Lista de PrinterDTO con las impresoras activas
     */
    @Override
    public List<PrinterDTO> obtenerImpresorasActivas() {
        try {
            // Usar el método helper de PrinterSpProperties para construir la query
            String sql = spProps.buildExecQuery(spProps.getGetActiveSp(), "");

            return jdbcTemplate.query(
                    sql,
                    (rs, rowNum) -> mapPrinterDTO(rs));
        } catch (Exception e) {
            logger.error("Error al obtener impresoras activas: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene una impresora específica por su ID (sin filtro de estado).
     * 
     * @param id ID de la impresora a buscar
     * @return PrinterDTO con los datos, o null si no existe
     */
    @Override
    public PrinterDTO obtenerImpresoraPorId(int id) {
        try {
            // Usar el método helper de PrinterSpProperties para construir la query
            String sql = spProps.buildExecQuery(spProps.getGetByIdSp(), "?");

            @SuppressWarnings("deprecation")
            List<PrinterDTO> lista = jdbcTemplate.query(
                    sql,
                    new Object[] { id },
                    (rs, rowNum) -> mapPrinterDTO(rs));
            return lista.isEmpty() ? null : lista.get(0);
        } catch (Exception e) {
            logger.debug("Impresora con ID {} no encontrada: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Crea una nueva impresora en la base de datos.
     * 
     * @param printerDTO Datos de la impresora a crear
     * @return PrinterDTO con la impresora creada (incluye ID y fecha de registro)
     */
    @Override
    public PrinterDTO crearImpresora(PrinterDTO printerDTO) {
        try {
            // Usar puerto 9100 si no se especifica
            int puerto = printerDTO.getPuerto() != null && printerDTO.getPuerto() > 0
                    ? printerDTO.getPuerto()
                    : 9100;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("PRI002", printerDTO.getNombre())
                    .addValue("PRI003", printerDTO.getIp())
                    .addValue("PRI004", printerDTO.getUbicacion())
                    .addValue("PRI005", printerDTO.getTipo())
                    .addValue("PRI006", printerDTO.getDescripcion())
                    .addValue("PRI007", LocalDateTime.now())
                    .addValue("PRI008", 1)
                    .addValue("PRI009", puerto);

            Map<String, Object> result = crearImpresoraCall.execute(params);

            if (result != null && !result.isEmpty()) {
                Object idObj = result.values().stream().findFirst().orElse(null);
                if (idObj instanceof Number) {
                    int idCreado = ((Number) idObj).intValue();
                    return obtenerImpresoraPorId(idCreado);
                }
            }
            return null;
        } catch (DataAccessException e) {
            logger.error("Error al crear impresora: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear la impresora", e);
        }
    }

    /**
     * Actualiza una impresora existente en la base de datos.
     * 
     * @param printerDTO Datos de la impresora a actualizar
     * @return PrinterDTO con la impresora actualizada
     */
    @Override
    public PrinterDTO actualizarImpresora(PrinterDTO printerDTO) {
        try {
            // Usar puerto 9100 si no se especifica
            int puerto = printerDTO.getPuerto() != null && printerDTO.getPuerto() > 0
                    ? printerDTO.getPuerto()
                    : 9100;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("PRI001", printerDTO.getId())
                    .addValue("PRI002", printerDTO.getNombre())
                    .addValue("PRI003", printerDTO.getIp())
                    .addValue("PRI004", printerDTO.getUbicacion())
                    .addValue("PRI005", printerDTO.getTipo())
                    .addValue("PRI006", printerDTO.getDescripcion())
                    .addValue("PRI008", printerDTO.getEstado())
                    .addValue("PRI009", puerto);

            Map<String, Object> result = actualizarImpresoraCall.execute(params);

            if (result != null && !result.isEmpty()) {
                // El SP retorna un SELECT con el registro actualizado
                // Recorrer los resultados y mapear el primero
                for (Object value : result.values()) {
                    if (value instanceof java.sql.ResultSet) {
                        java.sql.ResultSet rs = (java.sql.ResultSet) value;
                        if (rs.next()) {
                            return mapPrinterDTO(rs);
                        }
                    }
                }
            }
            return null;
        } catch (DataAccessException e) {
            logger.error("Error al actualizar impresora: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar la impresora", e);
        } catch (SQLException e) {
            logger.error("Error al procesar resultado de actualización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar la actualización", e);
        }
    }

    /**
     * Elimina lógicamente una impresora (cambio de estado a inactivo).
     * 
     * @param id ID de la impresora a eliminar lógicamente
     */
    @Override
    public void eliminarImpresora(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("PRI001", id);
        eliminarImpresoraCall.execute(params);
    }

    /**
     * Elimina definitivamente una impresora de la base de datos.
     * 
     * @param id ID de la impresora a eliminar definitivamente
     */
    @Override
    public void eliminarImpresoraDefinitivo(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("PRI001", id);
        eliminarImpresoraDefinitivoCall.execute(params);
    }

    /**
     * Mapea un registro del ResultSet a un objeto PrinterDTO.
     * 
     * @param rs ResultSet con los datos de la impresora retornados por el SP
     * @return PrinterDTO con los datos mapeados y listos para ser retornados
     * @throws SQLException Si ocurre un error al acceder al ResultSet
     */
    private PrinterDTO mapPrinterDTO(ResultSet rs) throws SQLException {
        PrinterDTO dto = new PrinterDTO();
        dto.setId(rs.getInt("PRI001"));
        dto.setNombre(rs.getString("PRI002"));
        dto.setIp(rs.getString("PRI003"));
        dto.setUbicacion(rs.getString("PRI004"));
        dto.setTipo(rs.getString("PRI005"));
        dto.setDescripcion(rs.getString("PRI006"));
        dto.setFechaRegistro(rs.getTimestamp("PRI007").toLocalDateTime());
        dto.setEstado(rs.getInt("PRI008"));
        // Mapear puerto con valor por defecto 9100 si es nulo
        try {
            Integer puerto = rs.getInt("PRI009");
            dto.setPuerto(puerto > 0 ? puerto : 9100);
        } catch (SQLException e) {
            logger.debug("Columna PRI009 no encontrada, usando puerto por defecto 9100");
            dto.setPuerto(9100);
        }
        return dto;
    }
}
