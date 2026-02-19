package com.example.backend_jovyweb.modules.produccion.service.impl;

import com.example.backend_jovyweb.modules.produccion.dto.EmpleadoDTO;
import com.example.backend_jovyweb.modules.produccion.model.Empleado;
import com.example.backend_jovyweb.modules.produccion.repository.EmpleadoRepository;
import com.example.backend_jovyweb.modules.produccion.service.EmpleadoService;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de implementación para gestionar empleados.
 * Proporciona funcionalidades para obtener empleados por diferentes criterios
 * y mapea entre modelos de BD y DTOs para el frontend.
 */
@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    /**
     * Constructor que inyecta el repositorio de empleados.
     * 
     * @param empleadoRepository Repositorio para acceso a datos de empleados
     */
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Obtiene la lista de todos los empleados.
     * 
     * @return Lista de DTOs de empleados mapeados
     */
    @Override
    public List<EmpleadoDTO> obtenerEmpleados() {
        // Obtener datos de la BD
        List<Empleado> empleados = empleadoRepository.obtenerEmpleados();

        // Mapear a DTO con nombres legibles para el frontend
        List<EmpleadoDTO> empleadosDTO = new ArrayList<>();
        for (Empleado empleado : empleados) {
            EmpleadoDTO dto = mapearEmpleadoADTO(empleado);
            empleadosDTO.add(dto);
        }

        return empleadosDTO;
    }

    /**
     * Obtiene un empleado específico por su código de empleado.
     * 
     * @param codigoEmpleado Código del empleado a buscar
     * @return Optional con el DTO del empleado si existe, vacío si no
     */
    @Override
    public Optional<EmpleadoDTO> obtenerEmpleadoPorCodigo(int codigoEmpleado) {
        return empleadoRepository.obtenerEmpleadoPorCodigo(codigoEmpleado);
    }

    /**
     * Mapea una entidad Empleado a su DTO.
     * Si necesitamos retornar más información:
     * 1. Agregar el campo setter en EmpleadoDTO (ej: dto.setArea(...))
     * 2. Agregar el getter del modelo aquí (ej: empleado.getAreD002())
     * 3. Verificar que el VIEW en schema.sql lo incluye
     * 4. La BD debe retornar el valor en el JSON
     * 
     * @param empleado Entidad de empleado
     * @return DTO del empleado con datos mapeados
     */
    private EmpleadoDTO mapearEmpleadoADTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getPerD001());
        dto.setNombre(empleado.getPerD002N());
        dto.setPuesto(empleado.getPueD002());
        return dto;
    }
}
