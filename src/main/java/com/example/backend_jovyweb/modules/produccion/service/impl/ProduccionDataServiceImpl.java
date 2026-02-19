package com.example.backend_jovyweb.modules.produccion.service.impl;

import com.example.backend_jovyweb.modules.produccion.dto.ProduccionDataDTO;
import com.example.backend_jovyweb.modules.produccion.repository.ProduccionDataRepository;
import com.example.backend_jovyweb.modules.produccion.service.ProduccionDataService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del servicio de datos consolidados de producción.
 * Proporciona métodos para obtener información de áreas, máquinas, empleados y
 * órdenes.
 */
@Service
public class ProduccionDataServiceImpl implements ProduccionDataService {

    private static final Logger logger = LoggerFactory.getLogger(ProduccionDataServiceImpl.class);

    private final ProduccionDataRepository produccionDataRepository;

    public ProduccionDataServiceImpl(ProduccionDataRepository produccionDataRepository) {
        this.produccionDataRepository = produccionDataRepository;
    }

    @Override
    public ProduccionDataDTO obtenerDatosProduccion(Integer proR011, Integer perD015) {
        logger.info("Obteniendo datos de producción con parámetros - ProR011: {}, PerD015: {}",
                proR011, perD015);

        ProduccionDataDTO datos = produccionDataRepository.obtenerDatosProduccion(proR011, perD015);

        logger.info("Datos obtenidos - Áreas: {}, Empleados: {}, Órdenes: {}",
                datos.getAreasMachines() != null ? datos.getAreasMachines().size() : 0,
                datos.getActiveEmployees() != null ? datos.getActiveEmployees().size() : 0,
                datos.getOpenOrders() != null ? datos.getOpenOrders().size() : 0);

        return datos;
    }

    @Override
    public ProduccionDataDTO obtenerTodosDatos() {
        logger.info("Obteniendo todos los datos de producción sin filtros");

        ProduccionDataDTO datos = produccionDataRepository.obtenerDatosProduccionSinFiltro();

        logger.info("Datos completos obtenidos - Áreas: {}, Empleados: {}, Órdenes: {}",
                datos.getAreasMachines() != null ? datos.getAreasMachines().size() : 0,
                datos.getActiveEmployees() != null ? datos.getActiveEmployees().size() : 0,
                datos.getOpenOrders() != null ? datos.getOpenOrders().size() : 0);

        return datos;
    }

}
