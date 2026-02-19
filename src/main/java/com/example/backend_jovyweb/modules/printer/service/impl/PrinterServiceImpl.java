package com.example.backend_jovyweb.modules.printer.service.impl;

import com.example.backend_jovyweb.modules.printer.dto.PrinterDTO;
import com.example.backend_jovyweb.modules.printer.repository.PrinterRepository;
import com.example.backend_jovyweb.modules.printer.service.PrinterService;
import com.example.backend_jovyweb.modules.printer.service.ZebraPrinterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de impresoras.
 * 
 * Responsabilidades:
 * - Llamar al repositorio para obtener/crear impresoras
 * - Aplicar validaciones de negocio
 * - Transformar datos si es necesario
 * - Retornar DTOs al controlador
 * - Integrar con servicio de Zebra para operaciones de impresión
 */
@Service
public class PrinterServiceImpl implements PrinterService {

    private final PrinterRepository printerRepository;
    private final ZebraPrinterService zebraPrinterService;

    /**
     * Constructor con inyección del repositorio de impresoras y servicio Zebra.
     * 
     * @param printerRepository   Repositorio para acceder a datos de impresoras
     * @param zebraPrinterService Servicio de integración con impresoras Zebra
     */
    public PrinterServiceImpl(PrinterRepository printerRepository, ZebraPrinterService zebraPrinterService) {
        this.printerRepository = printerRepository;
        this.zebraPrinterService = zebraPrinterService;
    }

    /**
     * Obtiene la lista de impresoras activas desde la base de datos.
     * 
     * @return Lista de PrinterDTO con las impresoras activas (estado = 1)
     */
    @Override
    public List<PrinterDTO> obtenerImpresorasActivas() {
        return printerRepository.obtenerImpresorasActivas();
    }

    /**
     * Crea una nueva impresora en el sistema.
     * 
     * @param printerDTO Datos de la impresora a crear
     * @return PrinterDTO con la impresora creada
     */
    @Override
    public PrinterDTO crearImpresora(PrinterDTO printerDTO) {
        return printerRepository.crearImpresora(printerDTO);
    }

    /**
     * Obtiene una impresora específica por su ID.
     * 
     * @param id ID de la impresora a buscar
     * @return Optional con la impresora si existe, vacío si no
     */
    @Override
    public Optional<PrinterDTO> obtenerImpresoraPorId(int id) {
        PrinterDTO printer = printerRepository.obtenerImpresoraPorId(id);
        return Optional.ofNullable(printer);
    }

    /**
     * Actualiza una impresora existente en el sistema.
     * 
     * @param printerDTO Datos de la impresora a actualizar
     * @return PrinterDTO con la impresora actualizada
     */
    @Override
    public PrinterDTO actualizarImpresora(PrinterDTO printerDTO) {
        PrinterDTO actualizada = printerRepository.actualizarImpresora(printerDTO);

        // Desconectar la impresora si está conectada, para forzar reconexión con nuevos
        // datos
        // Esto es importante si cambian IP, puerto u otros parámetros de conexión
        if (actualizada != null && actualizada.getId() != null) {
            zebraPrinterService.desconectarImpresoraEspecifica(actualizada.getId());
        }

        return actualizada;
    }

    /**
     * Elimina lógicamente una impresora (cambio de estado a inactivo).
     * 
     * @param id ID de la impresora a eliminar lógicamente
     */
    @Override
    public void eliminarImpresora(int id) {
        printerRepository.eliminarImpresora(id);
    }

    /**
     * Elimina definitivamente una impresora de la base de datos.
     * 
     * @param id ID de la impresora a eliminar definitivamente
     */
    @Override
    public void eliminarImpresoraDefinitivo(int id) {
        printerRepository.eliminarImpresoraDefinitivo(id);
    }

    /**
     * Imprime una etiqueta en una impresora específica usando ZPL.
     * 
     * @param printerId   ID de la impresora
     * @param templateZPL Plantilla ZPL de la etiqueta
     * @param cantidad    Número de copias a imprimir
     * @return true si la impresión fue exitosa
     */
    @Override
    public boolean imprimirEtiqueta(int printerId, String templateZPL, int cantidad) {
        Optional<PrinterDTO> printer = obtenerImpresoraPorId(printerId);

        if (printer.isEmpty()) {
            throw new IllegalArgumentException("Impresora con ID " + printerId + " no encontrada");
        }

        PrinterDTO printerDTO = printer.get();

        // Conectar a la impresora si no está conectada
        if (!zebraPrinterService.estaConectada(printerId)) {
            if (!zebraPrinterService.conectarImpresora(printerDTO)) {
                throw new RuntimeException("No se pudo conectar a la impresora: " + printerDTO.getNombre());
            }
        }

        // Imprimir la etiqueta
        return ((ZebraPrinterServiceImpl) zebraPrinterService).imprimirEtiqueta(printerId, templateZPL, cantidad);
    }

    /**
     * Envía un comando ZPL personalizado a una impresora específica.
     * 
     * @param printerId  ID de la impresora
     * @param comandoZPL Comando ZPL a enviar
     * @return true si el comando fue enviado exitosamente
     */
    @Override
    public boolean enviarComandoZPL(int printerId, String comandoZPL) {
        Optional<PrinterDTO> printer = obtenerImpresoraPorId(printerId);

        if (printer.isEmpty()) {
            throw new IllegalArgumentException("Impresora con ID " + printerId + " no encontrada");
        }

        PrinterDTO printerDTO = printer.get();

        // Conectar a la impresora si no está conectada
        if (!zebraPrinterService.estaConectada(printerId)) {
            if (!zebraPrinterService.conectarImpresora(printerDTO)) {
                throw new RuntimeException("No se pudo conectar a la impresora: " + printerDTO.getNombre());
            }
        }

        // Enviar el comando
        return ((ZebraPrinterServiceImpl) zebraPrinterService).enviarComandoZPL(printerId, comandoZPL);
    }

    /**
     * Obtiene el estado de una impresora específica.
     * 
     * @param printerId ID de la impresora
     * @return String con el estado de la impresora
     */
    @Override
    public String obtenerEstadoImpresora(int printerId) {
        Optional<PrinterDTO> printer = obtenerImpresoraPorId(printerId);

        if (printer.isEmpty()) {
            throw new IllegalArgumentException("Impresora con ID " + printerId + " no encontrada");
        }

        PrinterDTO printerDTO = printer.get();

        // Conectar a la impresora si no está conectada
        if (!zebraPrinterService.estaConectada(printerId)) {
            if (!zebraPrinterService.conectarImpresora(printerDTO)) {
                return "DESCONECTADA";
            }
        }

        // Obtener el estado
        return ((ZebraPrinterServiceImpl) zebraPrinterService).obtenerEstadoImpresora(printerId);
    }

    /**
     * Verifica si una impresora está conectada.
     * 
     * @param printerId ID de la impresora
     * @return true si está conectada
     */
    @Override
    public boolean estaImpresoraConectada(int printerId) {
        Optional<PrinterDTO> printer = obtenerImpresoraPorId(printerId);

        if (printer.isEmpty()) {
            throw new IllegalArgumentException("Impresora con ID " + printerId + " no encontrada");
        }

        PrinterDTO printerDTO = printer.get();

        // Intentar conectar si no está conectada
        if (!zebraPrinterService.estaConectada(printerId)) {
            zebraPrinterService.conectarImpresora(printerDTO);
        }

        return zebraPrinterService.estaConectada(printerId);
    }

}
