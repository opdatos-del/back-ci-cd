package com.example.backend_jovyweb.modules.produccion.repository;

import com.example.backend_jovyweb.modules.produccion.dto.ProduccionDataDTO;

/**
 * Repositorio para obtener datos consolidados de producción desde el SP
 * PROD_GDataPAPMPRPER.
 * Proporciona métodos para recuperar áreas, máquinas, empleados activos y
 * órdenes de productividad.
 */
public interface ProduccionDataRepository {

    /**
     * Obtiene los datos consolidados de producción (áreas, máquinas, empleados y
     * órdenes).
     * 
     * @param proR011 Estado de orden (puede ser null o un valor específico)
     * @param perD015 ID de empleado (puede ser null o un valor específico)
     * @return ProduccionDataDTO con las tres listas (áreas, empleados, órdenes)
     */
    ProduccionDataDTO obtenerDatosProduccion(Integer proR011, Integer perD015);

    /**
     * Obtiene los datos consolidados sin filtros (ambos parámetros null).
     * 
     * @return ProduccionDataDTO con los datos máximos disponibles
     */
    ProduccionDataDTO obtenerDatosProduccionSinFiltro();

}
