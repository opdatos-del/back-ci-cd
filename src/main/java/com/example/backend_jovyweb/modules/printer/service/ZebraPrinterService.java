package com.example.backend_jovyweb.modules.printer.service;

import com.example.backend_jovyweb.modules.printer.dto.PrinterDTO;

/**
 * Servicio para la integración con impresoras Zebra usando Link-OS SDK.
 * 
 * Responsabilidades:
 * - Conectarse a impresoras Zebra (Red/WiFi TCP)
 * - Enviar comandos ZPL (Zebra Programming Language)
 * - Enviar datos de impresión
 * - Manejo de errores de conexión y comunicación
 */
public interface ZebraPrinterService {
    /**
     * Conecta a una impresora Zebra específica.
     * 
     * @param printerDTO Datos de la impresora a conectar
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    boolean conectarImpresora(PrinterDTO printerDTO);

    /**
     * Desconecta de la impresora Zebra.
     * 
     * @return true si la desconexión fue exitosa
     */
    boolean desconectarImpresora();

    /**
     * Desconecta una impresora específica por su ID.
     * Útil cuando cambia la IP o puerto de la impresora.
     * 
     * @param printerId ID de la impresora a desconectar
     * @return true si la desconexión fue exitosa
     */
    boolean desconectarImpresoraEspecifica(int printerId);

    /**
     * Envía un comando ZPL a la impresora.
     * 
     * @param comando Comando ZPL a enviar
     * @return true si el comando se envió correctamente
     */
    boolean enviarComandoZPL(String comando);

    /**
     * Imprime una etiqueta usando ZPL.
     * 
     * @param templateZPL Plantilla ZPL de la etiqueta a imprimir
     * @param cantidad    Número de copias a imprimir
     * @return true si la impresión fue exitosa
     */
    boolean imprimirEtiqueta(String templateZPL, int cantidad);

    /**
     * Obtiene el estado de la impresora Zebra.
     * 
     * @return String con el estado de la impresora
     */
    String obtenerEstadoImpresora();

    /**
     * Verifica si la impresora está conectada.
     * 
     * @return true si está conectada, false en caso contrario
     */
    boolean estaConectada();

    /**
     * Verifica si una impresora específica está conectada.
     * 
     * @param printerId ID de la impresora
     * @return true si está conectada, false en caso contrario
     */
    boolean estaConectada(int printerId);

}
