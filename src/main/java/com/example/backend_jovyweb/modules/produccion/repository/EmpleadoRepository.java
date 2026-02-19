package com.example.backend_jovyweb.modules.produccion.repository;

import com.example.backend_jovyweb.modules.produccion.model.Empleado;
import com.example.backend_jovyweb.modules.produccion.dto.EmpleadoDTO;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de acceso a datos de empleados.
 * Define métodos para obtener empleados y buscar por código.
 */
public interface EmpleadoRepository {

    /**
     * Obtiene la lista de todos los empleados.
     * 
     * @return lista de empleados
     */
    List<Empleado> obtenerEmpleados();

    /**
     * Busca un empleado por su código único.
     * 
     * @param codigoEmpleado código del empleado
     * @return Optional con el DTO del empleado si existe
     */
    Optional<EmpleadoDTO> obtenerEmpleadoPorCodigo(int codigoEmpleado);
}
