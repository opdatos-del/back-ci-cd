package com.example.backend_jovyweb.modules.produccion.service;

import com.example.backend_jovyweb.modules.produccion.dto.ProduccionDataDTO;

/**
 * Servicio para datos consolidados de producción.
 * Coordina la obtención de áreas, máquinas, empleados y órdenes.
 */
public interface ProduccionDataService {

    /**
     * Obtiene los datos consolidados de producción.
     * 
     * @param proR011 Estado de orden (opcional)
     * @param perD015 ID de empleado (opcional)
     * @return ProduccionDataDTO con las tres listas
     */
    ProduccionDataDTO obtenerDatosProduccion(Integer proR011, Integer perD015);

    /**
     * Obtiene todos los datos de producción sin filtros.
     * 
     * @return ProduccionDataDTO completo
     */
    ProduccionDataDTO obtenerTodosDatos();

}
