package com.example.backend_jovyweb.modules.produccion.repository.impl;

import com.example.backend_jovyweb.modules.produccion.model.Area;
import com.example.backend_jovyweb.modules.produccion.model.Maquina;
import com.example.backend_jovyweb.modules.produccion.model.ProductividadRegistro;
import com.example.backend_jovyweb.modules.produccion.repository.ProductividadRegistroRepository;
import com.example.backend_jovyweb.config.sp.ProductivitySpProperties;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class ProductividadRegistroRepositoryImpl implements ProductividadRegistroRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductivitySpProperties spProps;

    public ProductividadRegistroRepositoryImpl(JdbcTemplate jdbcTemplate, ProductivitySpProperties spProps) {
        this.jdbcTemplate = jdbcTemplate;
        this.spProps = spProps;
    }

    @Override
    public Maquina obtenerMaquinaPorId(int maquinaId) {
        String sql = spProps.buildSelectQuery(
                "ProM001, ProM002, ProA001",
                spProps.getTableMachine(),
                "WHERE ProM001 = ?");
        RowMapper<Maquina> rowMapper = (rs, rowNum) -> {
            Maquina maquina = new Maquina();
            maquina.setProM001(rs.getInt("ProM001"));
            maquina.setProM002(rs.getString("ProM002"));
            maquina.setProA001(rs.getInt("ProA001"));
            return maquina;
        };
        List<Maquina> maquinas = jdbcTemplate.query(sql, new Object[] { maquinaId }, rowMapper);
        return maquinas.isEmpty() ? null : maquinas.get(0);
    }

    @Override
    public Area obtenerAreaPorId(int areaId) {
        String sql = spProps.buildSelectQuery(
                "ProA001, ProA002",
                spProps.getTableArea(),
                "WHERE ProA001 = ?");
        RowMapper<Area> rowMapper = (rs, rowNum) -> {
            Area area = new Area();
            area.setProA001(rs.getInt("ProA001"));
            area.setProA002(rs.getString("ProA002"));
            return area;
        };
        List<Area> areas = jdbcTemplate.query(sql, new Object[] { areaId }, rowMapper);
        return areas.isEmpty() ? null : areas.get(0);
    }

    @Override
    public ProductividadRegistro crearRegistro(ProductividadRegistro registro) {
        // Construir XML para pasar al SP
        String xml = construirXmlParaSP(registro);

        // Construir consulta EXEC usando la configuración centralizada
        String sql = spProps.buildExecQuery(spProps.getInsertRecordsSp(), "@xml = ?");
        String jsonResult = jdbcTemplate.queryForObject(sql, String.class, xml);

        // Verificar si el SP fue exitoso
        if (jsonResult == null || jsonResult.isEmpty()) {
            throw new RuntimeException(
                    String.format("SP %s retornó un resultado vacío", spProps.getInsertRecordsSp()));
        }

        // Parsear JSON para verificar éxito
        if (!jsonResult.contains("\"success\":true")) {
            throw new RuntimeException("Error al insertar registro: " + jsonResult);
        }

        // Si todo fue exitoso, retornar el registro
        return registro;
    }

    // Ejemplo alternativo usando SimpleJdbcCall para insertar con JSON o XML //
    // @Override
    // public ProductividadRegistro crearRegistro(ProductividadRegistro registro) {
    // // Si tu SP espera XML
    // String xml = construirXmlParaSP(registro);
    // SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
    // .withProcedureName("PROD_IProductivityRecords");
    // Map<String, Object> inParams = new HashMap<>();
    // inParams.put("xml", xml);
    // Map<String, Object> outParams = simpleJdbcCall.execute(inParams);
    // // Procesar outParams si el SP retorna algo
    // return registro;
    // }
    //
    // // Si tu SP espera un JSON en vez de XML:
    // @Override
    // public ProductividadRegistro crearRegistroConJson(ProductividadRegistro
    // registro) {
    // String json = construirJsonParaSP(registro); // Debes implementar este método
    // SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
    // .withProcedureName("PROD_IProductivityRecordsJson");
    // Map<String, Object> inParams = new HashMap<>();
    // inParams.put("json", json);
    // Map<String, Object> outParams = simpleJdbcCall.execute(inParams);
    // // Procesar outParams si el SP retorna algo
    // return registro;
    // }

    /**
     * Construye el XML requerido por el SP PROD_IProductivityRecords
     * 
     * @param registro Registro con los datos
     * @return String con el XML formateado
     */
    private String construirXmlParaSP(ProductividadRegistro registro) {
        StringBuilder xml = new StringBuilder();
        xml.append("<root>");
        xml.append("<row ");
        xml.append("ProR002=\"").append(registro.getProR002()).append("\" ");

        // Formatear ProR003 (fecha) - debe estar en formato YYYY-MM-DD
        if (registro.getProR003() != null) {
            xml.append("ProR003=\"").append(registro.getProR003().toString()).append("\" ");
        } else {
            xml.append("ProR003=\"\" ");
        }

        // Formatear ProR004 (hora) - debe estar en formato HH:MM:SS
        if (registro.getProR004() != null) {
            xml.append("ProR004=\"").append(registro.getProR004().toString()).append("\" ");
        } else {
            xml.append("ProR004=\"\" ");
        }

        xml.append("ProR005=\"").append(registro.getProR005()).append("\" ");

        xml.append("ProR006=\"").append(escaparXml(registro.getProR006())).append("\" ");
        xml.append("ProR007=\"").append(escaparXml(registro.getProR007())).append("\" ");
        xml.append("ProR008=\"").append(registro.getProR008()).append("\" ");
        xml.append("ProR009=\"").append(escaparXml(registro.getProR009())).append("\" ");
        xml.append("ProR010=\"").append(escaparXml(registro.getProR010())).append("\" ");
        xml.append("ProA001=\"").append(registro.getProA001()).append("\" ");
        xml.append("ProM001=\"").append(registro.getProM001()).append("\" ");
        xml.append("ProR011=\"").append(registro.getProR011()).append("\" ");
        xml.append("ProR012=\"").append(registro.getProR012()).append("\" ");
        xml.append("ProR013=\"").append(registro.getProR013()).append("\" ");
        xml.append("ProR014=\"").append(registro.getProR014()).append("\" ");
        xml.append("ProR015=\"").append(registro.getProR015()).append("\" ");
        xml.append("ProR016=\"").append(registro.getProR016()).append("\" ");
        xml.append("ProR017=\"").append(escaparXml(registro.getProR017())).append("\" ");
        xml.append("ProR018=\"").append(registro.getProR018()).append("\" ");
        xml.append("ProR019=\"").append(registro.getProR019()).append("\" ");
        xml.append("/>");
        xml.append("</root>");

        return xml.toString();
    }

    /**
     * Escapa caracteres especiales en XML
     * 
     * @param valor Texto a escapar
     * @return Texto escapado
     */
    private String escaparXml(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    @Override
    public List<ProductividadRegistro> obtenerRegistros() {
        String sql = "SELECT ProR001, ProR002, ProR003, ProR004, ProR005, ProR006, ProR007, ProR008, ProR009, " +
                "ProR010, ProA001, ProM001, ProR011, ProR012, ProR013, ProR014, ProR015, ProR016, ProR017, " +
                "ProR018, ProR019 FROM PROD_ProductivityRecords ORDER BY ProR001 DESC";

        RowMapper<ProductividadRegistro> rowMapper = obtenerRowMapperProductividadRegistro();
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * Obtiene los registros de productividad para una orden específica
     * Utiliza el SP PROD_GProductivityOrderDetail
     * 
     * @param numeroOrden Número de orden (ProR002)
     * @return Lista de registros de productividad para esa orden
     */
    public List<ProductividadRegistro> obtenerRegistrosPorNumeroOrden(int numeroOrden) {
        // Construir la consulta EXEC usando la configuración centralizada
        String sql = spProps.buildExecQuery(
                spProps.getGetOrderDetailSp(),
                "@ProR002 = ?");

        return jdbcTemplate.query(
                sql,
                new Object[] { numeroOrden },
                obtenerRowMapperProductividadRegistro());
    }

    /**
     * RowMapper reutilizable para mapear resultados a ProductividadRegistro
     */
    private RowMapper<ProductividadRegistro> obtenerRowMapperProductividadRegistro() {
        return (rs, rowNum) -> {
            ProductividadRegistro reg = new ProductividadRegistro();
            reg.setProR001(rs.getInt("ProR001"));
            reg.setProR002(rs.getInt("ProR002"));
            reg.setProR003(rs.getDate("ProR003") != null ? rs.getDate("ProR003").toLocalDate() : null);
            reg.setProR004(rs.getTime("ProR004") != null ? rs.getTime("ProR004").toLocalTime() : null);
            reg.setProR005(rs.getInt("ProR005"));
            reg.setProR006(rs.getString("ProR006"));
            reg.setProR007(rs.getString("ProR007"));
            reg.setProR008(rs.getInt("ProR008"));
            reg.setProR009(rs.getString("ProR009"));
            reg.setProR010(rs.getString("ProR010"));
            reg.setProA001(rs.getInt("ProA001"));
            reg.setProM001(rs.getInt("ProM001"));
            reg.setProR011(rs.getInt("ProR011"));
            reg.setProR012(rs.getInt("ProR012"));
            reg.setProR013(rs.getInt("ProR013"));
            reg.setProR014(rs.getInt("ProR014"));
            reg.setProR015(rs.getInt("ProR015"));
            reg.setProR016(rs.getInt("ProR016"));
            reg.setProR017(rs.getString("ProR017"));
            reg.setProR018(rs.getInt("ProR018"));
            reg.setProR019(rs.getInt("ProR019"));
            return reg;
        };
    }

    /**
     * Obtiene datos consolidados de producción: áreas, máquinas, empleados y
     * órdenes abiertas
     * Llama al SP PROD_GDataPAPMPRPER que retorna JSON combinado
     * 
     * @param proR011 Status de orden (opcional, 0 para traer todas)
     * @param perD015 ID Empleados (opcional, 1 por defecto)
     * @return JSON con estructura: {"PROD_ARMACH":[...], "PROD_ACTEMP":[...],
     *         "PROD_OP":[...]}
     */
    public String obtenerDatosProduccion(Integer proR011, Integer perD015) {
        Integer statusOrden = proR011 != null ? proR011 : 0;
        Integer idEmpleados = perD015 != null ? perD015 : 1;

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PROD_GDataPAPMPRPER");

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("ProR011", statusOrden);
        inParams.put("PerD015", idEmpleados);

        Map<String, Object> outParams = simpleJdbcCall.execute(inParams);

        // Si el SP retorna un resultSet, extraerlo; si retorna un valor escalar
        if (outParams.containsKey("results")) {
            List<?> results = (List<?>) outParams.get("results");
            return results.isEmpty() ? "{}" : results.get(0).toString();
        }

        return "{}";
    }

}
