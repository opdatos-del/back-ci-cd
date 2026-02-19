package com.example.backend_jovyweb.modules.produccion.repository;

import com.example.backend_jovyweb.modules.produccion.model.Area;
import com.example.backend_jovyweb.modules.produccion.model.Maquina;
import com.example.backend_jovyweb.modules.produccion.model.ProductividadRegistro;
import java.util.List;

/**
 * Repositorio para operaciones de acceso a datos de registros de productividad.
 * Define métodos para acceder a máquinas, áreas y registros de productividad.
 */
public interface ProductividadRegistroRepository {

    /**
     * Obtiene una máquina específica por su ID.
     * 
     * @param maquinaId ID de la máquina
     * @return Máquina encontrada o null
     */
    Maquina obtenerMaquinaPorId(int maquinaId);

    /**
     * Obtiene un área específica por su ID.
     * 
     * @param areaId ID del área
     * @return Área encontrada o null
     */
    Area obtenerAreaPorId(int areaId);

    /**
     * Crea un nuevo registro de productividad.
     * 
     * @param registro entidad de registro de productividad
     * @return registro creado
     */
    ProductividadRegistro crearRegistro(ProductividadRegistro registro);

    /**
     * Obtiene todos los registros de productividad.
     * 
     * @return lista de registros
     */
    List<ProductividadRegistro> obtenerRegistros();

    /**
     * Obtiene todos los registros de productividad para una orden específica.
     * Utiliza el SP PROD_GProductivityOrderDetail
     * 
     * @param numeroOrden Número de orden (ProR002)
     * @return lista de registros para esa orden
     */
    List<ProductividadRegistro> obtenerRegistrosPorNumeroOrden(int numeroOrden);

    /**
     * Obtiene datos consolidados de producción (áreas, máquinas, empleados, órdenes
     * abiertas)
     * Llama al SP PROD_GDataPAPMPRPER que retorna JSON combinado
     * 
     * @param proR011 Status de orden (opcional)
     * @param perD015 ID Empleados (opcional)
     * @return JSON con estructura: {"PROD_ARMACH":[...], "PROD_ACTEMP":[...],
     *         "PROD_OP":[...]}
     */
    String obtenerDatosProduccion(Integer proR011, Integer perD015);
}
