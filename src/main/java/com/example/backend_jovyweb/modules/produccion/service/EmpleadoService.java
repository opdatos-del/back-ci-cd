package com.example.backend_jovyweb.modules.produccion.service;

import com.example.backend_jovyweb.modules.produccion.dto.EmpleadoDTO;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de empleados.
 * Define los contratos para las operaciones con empleados.
 */
public interface EmpleadoService {
    /**
     * Obtiene la lista de todos los empleados.
     * 
     * @return Lista de DTOs de empleados
     */
    List<EmpleadoDTO> obtenerEmpleados();

    /**
     * Obtiene un empleado específico por su código de empleado.
     * 
     * @param codigoEmpleado Código del empleado
     * @return Optional con el DTO del empleado si existe, vacío si no
     */
    Optional<EmpleadoDTO> obtenerEmpleadoPorCodigo(int codigoEmpleado);
}
