package com.example.backend_jovyweb.modules.printer.controller;

import com.example.backend_jovyweb.modules.printer.dto.PrinterDTO;
import com.example.backend_jovyweb.modules.printer.dto.PrintJobDTO;
import com.example.backend_jovyweb.modules.printer.dto.ZplCommandDTO;
import com.example.backend_jovyweb.modules.printer.service.PrinterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para la gestión de impresoras y funciones de impresión con
 * Zebra SDK.
 */
@RestController
@RequestMapping("/api/printers")
@Tag(name = "Impresoras", description = "API para gestionar impresoras e imprimir etiquetas con Zebra SDK")
@Validated
public class PrinterController {

        private final PrinterService printerService;

        /**
         * Constructor con inyección del servicio de impresoras.
         * 
         * @param printerService Servicio para operaciones de impresoras
         */
        public PrinterController(PrinterService printerService) {
                this.printerService = printerService;
        }

        /**
         * Obtiene la lista de todas las impresoras activas.
         * 
         * @return Lista de PrinterDTO en formato JSON
         */
        @GetMapping
        @Operation(summary = "Obtener impresoras activas", description = "Retorna una lista JSON de todas las impresoras activas en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de impresoras en JSON"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public List<PrinterDTO> obtenerImpresoras() {
                return printerService.obtenerImpresorasActivas();
        }

        /**
         * Crea una nueva impresora en el sistema.
         * 
         * @param printerDTO Datos de la impresora en JSON (body de la petición)
         * @return ResponseEntity con la impresora creada en formato JSON
         */
        @PostMapping
        @Operation(summary = "Crear nueva impresora", description = "Registra una nueva impresora en la base de datos y retorna los datos creados en JSON")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Impresora creada exitosamente en JSON"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos en el JSON")
        })
        public ResponseEntity<PrinterDTO> crearImpresora(
                        @Parameter(description = "Datos de la impresora en JSON", required = true) @Valid @RequestBody PrinterDTO printerDTO) {
                PrinterDTO creada = printerService.crearImpresora(printerDTO);
                return ResponseEntity.ok(creada);
        }

        /**
         * Obtiene una impresora específica por su ID.
         * 
         * @param id ID de la impresora a buscar
         * @return ResponseEntity con la impresora encontrada en JSON, o 404 si no
         *         existe
         */
        @GetMapping("/{id}")
        @Operation(summary = "Obtener impresora por ID", description = "Retorna los datos en JSON de una impresora específica")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Impresora encontrada en JSON"),
                        @ApiResponse(responseCode = "404", description = "Impresora no encontrada")
        })
        public ResponseEntity<PrinterDTO> obtenerImpresoraPorId(
                        @Parameter(description = "ID de la impresora", example = "1", required = true) @PathVariable int id) {
                return printerService.obtenerImpresoraPorId(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        /**
         * Actualiza una impresora existente en el sistema.
         * 
         * @param printerDTO Datos actualizados de la impresora en JSON
         * @return ResponseEntity con la impresora actualizada en formato JSON
         */
        @PutMapping
        @Operation(summary = "Actualizar impresora", description = "Actualiza los datos de una impresora existente y retorna los datos actualizados en JSON")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Impresora actualizada exitosamente en JSON"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos en el JSON")
        })
        public ResponseEntity<PrinterDTO> actualizarImpresora(
                        @Parameter(description = "Datos actualizados de la impresora en JSON", required = true) @Valid @RequestBody PrinterDTO printerDTO) {
                PrinterDTO actualizada = printerService.actualizarImpresora(printerDTO);
                return ResponseEntity.ok(actualizada);
        }

        /**
         * Elimina lógicamente una impresora (cambio de estado a inactivo).
         * 
         * @param id ID de la impresora a eliminar
         * @return ResponseEntity sin contenido
         */
        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar impresora (lógico)", description = "Desactiva una impresora (cambio de estado a inactivo)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Impresora desactivada exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Impresora no encontrada")
        })
        public ResponseEntity<Void> eliminarImpresora(
                        @Parameter(description = "ID de la impresora", example = "1", required = true) @PathVariable int id) {
                printerService.eliminarImpresora(id);
                return ResponseEntity.noContent().build();
        }

        /**
         * Elimina definitivamente una impresora de la base de datos.
         * 
         * @param id ID de la impresora a eliminar definitivamente
         * @return ResponseEntity sin contenido
         */
        @DeleteMapping("/{id}/definitivo")
        @Operation(summary = "Eliminar impresora (definitivo)", description = "Elimina permanentemente una impresora de la base de datos")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Impresora eliminada definitivamente"),
                        @ApiResponse(responseCode = "404", description = "Impresora no encontrada")
        })
        public ResponseEntity<Void> eliminarImpresoraDefinitivo(
                        @Parameter(description = "ID de la impresora", example = "1", required = true) @PathVariable int id) {
                printerService.eliminarImpresoraDefinitivo(id);
                return ResponseEntity.noContent().build();
        }

        /**
         * Imprime una etiqueta usando una plantilla ZPL en la impresora especificada.
         * 
         * @param printJobDTO Datos del trabajo de impresión (printerId, templateZPL,
         *                    cantidad)
         * @return ResponseEntity con el resultado de la impresión
         */
        @PostMapping("/print/label")
        @Operation(summary = "Imprimir etiqueta ZPL", description = "Imprime una etiqueta usando una plantilla ZPL en la impresora Zebra especificada (conexión TCP/WiFi)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Etiqueta impresa exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos o impresora no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error al imprimir")
        })
        public ResponseEntity<Map<String, Object>> imprimirEtiqueta(
                        @Parameter(description = "Datos del trabajo de impresión en JSON", required = true) @Valid @RequestBody PrintJobDTO printJobDTO) {
                try {
                        boolean resultado = printerService.imprimirEtiqueta(
                                        printJobDTO.getPrinterId(),
                                        printJobDTO.getTemplateZPL(),
                                        printJobDTO.getCantidad());

                        Map<String, Object> response = new HashMap<>();
                        response.put("PRI_Exito", resultado);
                        response.put("PRI_Mensaje",
                                        resultado ? "Etiqueta impresa exitosamente" : "Error al imprimir la etiqueta");
                        response.put("PRI_IdImp", printJobDTO.getPrinterId());
                        response.put("PRI_Cantidad", printJobDTO.getCantidad());

                        return ResponseEntity.ok(response);
                } catch (IllegalArgumentException e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Exito", false);
                        error.put("PRI_Error", e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                } catch (Exception e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Exito", false);
                        error.put("PRI_Error", "Error interno: " + e.getMessage());
                        return ResponseEntity.internalServerError().body(error);
                }
        }

        /**
         * Envía un comando ZPL personalizado a una impresora específica.
         * 
         * @param zplCommandDTO Datos del comando (printerId y comando ZPL)
         * @return ResponseEntity con el resultado del comando
         */
        @PostMapping("/print/command")
        @Operation(summary = "Enviar comando ZPL personalizado", description = "Envía un comando ZPL personalizado directamente a la impresora. Útil para configuraciones personalizadas o pruebas")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comando enviado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos o impresora no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error al enviar el comando")
        })
        public ResponseEntity<Map<String, Object>> enviarComandoZPL(
                        @Parameter(description = "Datos del comando ZPL en JSON", required = true) @Valid @RequestBody ZplCommandDTO zplCommandDTO) {
                try {
                        boolean resultado = printerService.enviarComandoZPL(
                                        zplCommandDTO.getPrinterId(),
                                        zplCommandDTO.getComando());

                        Map<String, Object> response = new HashMap<>();
                        response.put("PRI_Exito", resultado);
                        response.put("PRI_Mensaje",
                                        resultado ? "Comando ZPL enviado exitosamente" : "Error al enviar el comando");
                        response.put("PRI_IdImp", zplCommandDTO.getPrinterId());
                        response.put("PRI_Comando", zplCommandDTO.getComando());

                        return ResponseEntity.ok(response);
                } catch (IllegalArgumentException e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Exito", false);
                        error.put("PRI_Error", e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                } catch (Exception e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Exito", false);
                        error.put("PRI_Error", "Error interno: " + e.getMessage());
                        return ResponseEntity.internalServerError().body(error);
                }
        }

        /**
         * Obtiene el estado actual de una impresora Zebra.
         * 
         * @param id ID de la impresora
         * @return ResponseEntity con el estado de la impresora
         */
        @GetMapping("/{id}/status")
        @Operation(summary = "Obtener estado de la impresora", description = "Obtiene el estado actual de una impresora Zebra específica")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Impresora no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener el estado")
        })
        public ResponseEntity<Map<String, Object>> obtenerEstado(
                        @Parameter(description = "ID de la impresora", example = "1", required = true) @PathVariable int id) {
                try {
                        String estado = printerService.obtenerEstadoImpresora(id);

                        Map<String, Object> response = new HashMap<>();
                        response.put("PRI_IdImp", id);
                        response.put("PRI_Estado", estado);

                        return ResponseEntity.ok(response);
                } catch (IllegalArgumentException e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Error", e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                } catch (Exception e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Error", "Error interno: " + e.getMessage());
                        return ResponseEntity.internalServerError().body(error);
                }
        }

        /**
         * Verifica si una impresora está conectada.
         * 
         * @param id ID de la impresora
         * @return ResponseEntity con el estado de conexión
         */
        @GetMapping("/{id}/connected")
        @Operation(summary = "Verificar conexión de la impresora", description = "Verifica si una impresora Zebra está conectada y disponible")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estado de conexión obtenido"),
                        @ApiResponse(responseCode = "404", description = "Impresora no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error al verificar la conexión")
        })
        public ResponseEntity<Map<String, Object>> verificarConexion(
                        @Parameter(description = "ID de la impresora", example = "1", required = true) @PathVariable int id) {
                try {
                        boolean conectada = printerService.estaImpresoraConectada(id);

                        Map<String, Object> response = new HashMap<>();
                        response.put("PRI_IdImp", id);
                        response.put("PRI_Conectada", conectada);

                        return ResponseEntity.ok(response);
                } catch (IllegalArgumentException e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Error", e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                } catch (Exception e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("PRI_Error", "Error interno: " + e.getMessage());
                        return ResponseEntity.internalServerError().body(error);
                }
        }
}
