package com.example.backend_jovyweb.modules.produccion.service;

import com.example.backend_jovyweb.modules.produccion.dto.ProductividadRegistroDTO;
import com.example.backend_jovyweb.modules.produccion.dto.ProductividadRegistroDetalleDTO;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de productividad.
 * Define los contratos para crear, consultar y obtener detalles de registros de
 * productividad.
 */
public interface ProductividadRegistroService {
    /**
     * Crea un nuevo registro de productividad.
     * 
     * @param registro DTO del registro a crear
     * @return DTO del registro creado con ID asignado
     */
    ProductividadRegistroDTO crearRegistro(ProductividadRegistroDTO registro);

    /**
     * Obtiene la lista de todos los registros de productividad guardados.
     * 
     * @return Lista de DTOs de registros de productividad
     */
    List<ProductividadRegistroDTO> obtenerRegistros();

    /**
     * Crea un nuevo registro de productividad a partir de DTO XML deserializado.
     * 
     * @param xmlDto DTO parseado del XML con campos ProR002-ProR019
     * @return DTO del registro creado con ID asignado
     */
    ProductividadRegistroDTO crearRegistroDesdeXmlDto(
            com.example.backend_jovyweb.modules.produccion.dto.RegistroProductividadXmlDTO xmlDto);

    /**
     * Obtiene el detalle completo de una orden por su número de orden.
     * Busca el primer registro asociado a ese número de orden.
     * 
     * @param numeroOrden Número de la orden de producción (ProR002)
     * @return Optional con el DTO detallado si existe, vacío si no
     */
    Optional<ProductividadRegistroDetalleDTO> obtenerDetalleRegistroPorNumeroOrden(int numeroOrden);

    /**
     * Obtiene todos los detalles de registros para una orden de producción
     * específica.
     * Retorna todos los registros asociados a ese número de orden.
     * 
     * @param numeroOrden Número de la orden de producción (ProR002)
     * @return Lista de DTOs detallados de todos los registros de esa orden
     */
    List<ProductividadRegistroDetalleDTO> obtenerDetallesRegistrosPorNumeroOrden(int numeroOrden);
}
