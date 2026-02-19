package com.example.backend_jovyweb.modules.printer.repository;

import com.example.backend_jovyweb.modules.printer.dto.PrinterDTO;
import java.util.List;

/**
 * Repositorio para operaciones de impresoras.
 */
public interface PrinterRepository {
    /**
     * Obtiene la lista de impresoras activas desde la base de datos.
     * 
     * @return Lista de PrinterDTO con las impresoras activas
     */
    List<PrinterDTO> obtenerImpresorasActivas();

    /**
     * Obtiene una impresora específica por su ID (sin filtro de estado).
     * 
     * @param id ID de la impresora a buscar
     * @return PrinterDTO con los datos de la impresora, o null si no existe
     */
    PrinterDTO obtenerImpresoraPorId(int id);

    /**
     * Crea una nueva impresora en la base de datos.
     * 
     * @param printerDTO Datos de la impresora a crear
     * @return PrinterDTO con la impresora creada (incluye ID y fecha de registro)
     */
    PrinterDTO crearImpresora(PrinterDTO printerDTO);

    /**
     * Actualiza una impresora existente en la base de datos.
     * 
     * @param printerDTO Datos de la impresora a actualizar
     * @return PrinterDTO con la impresora actualizada
     */
    PrinterDTO actualizarImpresora(PrinterDTO printerDTO);

    /**
     * Elimina lógicamente una impresora (cambia estado a inactivo).
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
}
