package com.example.backend_jovyweb.modules.produccion.controller;

import com.example.backend_jovyweb.modules.produccion.dto.EmpleadoDTO;
import com.example.backend_jovyweb.modules.produccion.service.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Empleados", description = "API para gestionar empleados")
/**
 * Controller para gestionar endpoints de empleados.
 * Proporciona operaciones para obtener empleados por diferentes criterios.
 */
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    /**
     * Constructor que inyecta el servicio de empleados.
     * 
     * @param empleadoService Servicio para operaciones con empleados
     */
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los empleados", description = "Retorna una lista de todos los empleados registrados en el sistema")
    public List<EmpleadoDTO> obtenerEmpleados() {
        return empleadoService.obtenerEmpleados();
    }

    @GetMapping("/codigo/{codigoEmpleado}")
    @Operation(summary = "Obtener empleado por código", description = "Retorna los datos de un empleado específico mediante su código de empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmpleadoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorCodigo(
            @Parameter(description = "Código único del empleado", example = "1", required = true) @PathVariable int codigoEmpleado) {
        Optional<EmpleadoDTO> empleado = empleadoService.obtenerEmpleadoPorCodigo(codigoEmpleado);
        return empleado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
