package com.example.backend_jovyweb.modules.printer.service;

import com.example.backend_jovyweb.modules.printer.dto.PrinterDTO;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para operaciones y lógica de negocio de impresoras.
 */
public interface PrinterService {
    /**
     * Obtiene la lista de impresoras activas.
     * 
     * @return Lista de PrinterDTO con las impresoras activas
     */
    List<PrinterDTO> obtenerImpresorasActivas();

    /**
     * Crea una nueva impresora en el sistema.
     * 
     * @param printerDTO Datos de la impresora a crear
     * @return PrinterDTO con la impresora creada
     */
    PrinterDTO crearImpresora(PrinterDTO printerDTO);

    /**
     * Obtiene una impresora específica por su ID.
     * 
     * @param id ID de la impresora a buscar
     * @return Optional con la impresora si existe, vacío si no
     */
    Optional<PrinterDTO> obtenerImpresoraPorId(int id);

    /**
     * Actualiza una impresora existente en el sistema.
     * 
     * @param printerDTO Datos de la impresora a actualizar
     * @return PrinterDTO con la impresora actualizada
     */
    PrinterDTO actualizarImpresora(PrinterDTO printerDTO);

    /**
     * Elimina lógicamente una impresora (cambio de estado a inactivo).
     * 
     * @param id ID de la impresora a eliminar lógicamente
     */
    void eliminarImpresora(int id);

    /**
     * Elimina definitivamente una impresora de la base de datos.
     * 
     * @param id ID de la impresora a eliminar definitivamente
     */
    void eliminarImpresoraDefinitivo(int id);

    /**
     * Imprime una etiqueta en una impresora específica usando ZPL.
     * 
     * @param printerId   ID de la impresora
     * @param templateZPL Plantilla ZPL de la etiqueta
     * @param cantidad    Número de copias a imprimir
     * @return true si la impresión fue exitosa
     */
    boolean imprimirEtiqueta(int printerId, String templateZPL, int cantidad);

    /**
     * Envía un comando ZPL personalizado a una impresora específica.
     * 
     * @param printerId  ID de la impresora
     * @param comandoZPL Comando ZPL a enviar
     * @return true si el comando fue enviado exitosamente
     */
    boolean enviarComandoZPL(int printerId, String comandoZPL);

    /**
     * Obtiene el estado de una impresora específica.
     * 
     * @param printerId ID de la impresora
     * @return String con el estado de la impresora
     */
    String obtenerEstadoImpresora(int printerId);

    /**
     * Verifica si una impresora está conectada.
     * 
     * @param printerId ID de la impresora
     * @return true si está conectada
     */
    boolean estaImpresoraConectada(int printerId);
}