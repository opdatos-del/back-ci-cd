package com.example.backend_jovyweb.modules.produccion.controller;

import com.example.backend_jovyweb.modules.produccion.dto.ProductividadRegistroDTO;
import com.example.backend_jovyweb.modules.produccion.dto.RegistroProductividadXmlDTO;
import com.example.backend_jovyweb.modules.produccion.dto.ProductividadRegistroDetalleDTO;
import com.example.backend_jovyweb.modules.produccion.service.ProductividadRegistroService;
import com.example.backend_jovyweb.modules.produccion.service.ProduccionDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productividad")
@Tag(name = "Productividad", description = "API para gestionar registros de productividad")
/**
 * Controller para gestionar endpoints de productividad.
 * Proporciona operaciones para crear, consultar y obtener detalles de registros
 * de productividad.
 */
public class ProductividadRegistroController {

        private final ProductividadRegistroService productividadService;
        private final ProduccionDataService produccionDataService;

        /**
         * Constructor que inyecta los servicios de productividad.
         * 
         * @param productividadService  Servicio para operaciones de productividad
         * @param produccionDataService Servicio para datos consolidados de producción
         */
        public ProductividadRegistroController(ProductividadRegistroService productividadService,
                        ProduccionDataService produccionDataService) {
                this.productividadService = productividadService;
                this.produccionDataService = produccionDataService;
        }

        /*
         * ENDPOINT JSON (COMENTADO - Para uso futuro)
         * =============================================
         * Este endpoint está comentado porque por ahora se utilizará XML
         * para enviar registros de productividad.
         * Se dejó el código para reutilizarlo cuando migremos a JSON
         * en futuras versiones del sistema.
         * 
         * @PostMapping(value = "/registros", consumes = "application/json", produces =
         * "application/json")
         * 
         * @Operation(summary = "Crear registro de productividad (JSON)", description =
         * "Guarda un nuevo registro de productividad usando formato JSON")
         * 
         * @ApiResponses(value = {
         * 
         * @ApiResponse(responseCode = "200", description =
         * "Registro creado exitosamente", content = @Content(mediaType =
         * "application/json", schema = @Schema(implementation =
         * ProductividadRegistroDTO.class))),
         * 
         * @ApiResponse(responseCode = "400", description =
         * "Datos inválidos o faltantes")
         * })
         * public ProductividadRegistroDTO crearRegistroJSON(@Valid @RequestBody
         * ProductividadRegistroDTO registro) {
         * return productividadService.crearRegistro(registro);
         * }
         */

        @PostMapping(value = "/registros", consumes = "application/xml", produces = "application/json")
        @Operation(summary = "Crear registro de productividad (XML)", description = "Guarda un nuevo registro de productividad usando formato XML. Estructura: <root><row ProR002=\"...\" ProR003=\"...\" .../></root>")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Registro creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductividadRegistroDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos o faltantes")
        })
        public ProductividadRegistroDTO crearRegistroXML(@RequestBody RegistroProductividadXmlDTO xmlRegistro) {
                return productividadService.crearRegistroDesdeXmlDto(xmlRegistro);
        }

        @GetMapping("/registros")
        @Operation(summary = "Obtener registros de productividad", description = "Sin parámetros: retorna lista de todos los registros. Con parámetros: retorna datos consolidados (áreas, máquinas, empleados activos, órdenes abiertas)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Datos obtenidos exitosamente")
        })
        public Object obtenerRegistros(
                        @Parameter(description = "ID de la orden de producción (opcional)", example = "1") @RequestParam(name = "proR011", required = false) Integer orderId,
                        @Parameter(description = "ID del período (opcional)", example = "1") @RequestParam(name = "perD015", required = false) Integer periodId) {

                // Si se proporcionan parámetros, devuelve datos consolidados del SP
                if (orderId != null || periodId != null) {
                        return produccionDataService.obtenerDatosProduccion(orderId, periodId);
                }

                // Si no hay parámetros, devuelve todos los registros de productividad
                return productividadService.obtenerRegistros();
        }

        @GetMapping("/registros/numero-orden/{numeroOrden}/detalle")
        @Operation(summary = "Obtener detalle de una orden de producción", description = "Retorna todos los registros de productividad asociados a una orden, incluyendo: datos básicos, métricas de productividad, minutos perdidos/ganados, supervisor, máquina, área y otras métricas")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Detalles de la orden encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductividadRegistroDetalleDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Orden de producción no encontrada")
        })
        public ResponseEntity<List<ProductividadRegistroDetalleDTO>> obtenerDetalleOrden(
                        @Parameter(description = "Número de orden de producción", example = "2025", required = true) @PathVariable int numeroOrden) {
                List<ProductividadRegistroDetalleDTO> detalles = productividadService
                                .obtenerDetallesRegistrosPorNumeroOrden(numeroOrden);

                if (detalles.isEmpty()) {
                        return ResponseEntity.notFound().build();
                }

                return ResponseEntity.ok(detalles);
        }

}
