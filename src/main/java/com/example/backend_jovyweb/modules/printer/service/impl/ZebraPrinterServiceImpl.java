package com.example.backend_jovyweb.modules.printer.service.impl;

import com.example.backend_jovyweb.modules.printer.dto.PrinterDTO;
import com.example.backend_jovyweb.modules.printer.service.ZebraPrinterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del servicio de integración con impresoras Zebra.
 * 
 * Soporta múltiples tipos de conexión:
 * - WiFi (TCP): tcp://192.168.1.100:9100
 * 
 * Utiliza sockets TCP para proyectos de WiFi/red.
 * 
 * Mantiene un pool de conexiones independientes para cada impresora,
 * permitiendo gestionar varios dispositivos en paralelo.
 */
@Service
public class ZebraPrinterServiceImpl implements ZebraPrinterService {

    private static final Logger logger = LoggerFactory.getLogger(ZebraPrinterServiceImpl.class);
    private static final int PUERTO_DEFECTO = 9100;

    /**
     * Clase interna para representar una conexión TCP a una impresora.
     */
    private static class PrinterConnection {
        Socket socket;
        PrintWriter writer;
        BufferedReader reader;
        boolean conectada;
        String ip;
        int puerto;
        String nombre;
        String tipo;

        PrinterConnection(String ip, int puerto, String nombre, String tipo) {
            this.ip = ip;
            this.puerto = puerto;
            this.nombre = nombre;
            this.tipo = tipo;
            this.conectada = false;
        }

        boolean isValid() {
            return conectada && socket != null && !socket.isClosed();
        }

        void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                if (writer != null)
                    writer.close();
                if (reader != null)
                    reader.close();
                socket.close();
            }
            conectada = false;
        }
    }

    // Conexión actual
    private PrinterConnection conexionActual = null;

    // Map para gestionar múltiples impresoras por ID
    private final Map<Integer, PrinterConnection> conexiones = new ConcurrentHashMap<>();

    @Override
    public boolean conectarImpresora(PrinterDTO printerDTO) {
        try {
            if (printerDTO == null || printerDTO.getId() == null) {
                logger.error("PrinterDTO o ID es nulo");
                return false;
            }

            int printerId = printerDTO.getId();
            String tipo = printerDTO.getTipo() != null ? printerDTO.getTipo() : "TCP";
            String ip = printerDTO.getIp();
            int puerto = printerDTO.getPuerto() != null ? printerDTO.getPuerto() : PUERTO_DEFECTO;

            logger.info("Intentando conectar a impresora [{}]: {} (tipo: {})", printerId,
                    printerDTO.getNombre(), tipo);

            // Desconectar anterior si existe
            desconectarImpresoraInterno(printerId);

            // Crear nueva conexión
            PrinterConnection conexion = new PrinterConnection(ip, puerto, printerDTO.getNombre(), tipo);

            // Conectar según el tipo
            switch (tipo.toUpperCase()) {
                case "TCP":
                case "WIFI":
                case "RED":
                    if (ip == null || ip.trim().isEmpty()) {
                        logger.error("IP es requerida para conexión TCP/WiFi");
                        return false;
                    }
                    conectarTCP(conexion, ip, puerto);
                    break;

                default:
                    logger.warn("Tipo de conexión desconocido: {}, usando TCP", tipo);
                    conectarTCP(conexion, ip, puerto);
                    break;
            }

            conexiones.put(printerId, conexion);
            this.conexionActual = conexion;

            logger.info("Conexión exitosa con impresora [{}]: {} ({})", printerId,
                    printerDTO.getNombre(), tipo);
            return true;

        } catch (Exception e) {
            logger.error("Error al conectar con la impresora: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Conecta vía TCP/IP (WiFi, Ethernet).
     */
    private void conectarTCP(PrinterConnection conexion, String host, int puerto) throws IOException {
        logger.debug("Conectando por TCP a {}:{}", host, puerto);
        conexion.socket = new Socket(host, puerto);
        conexion.socket.setKeepAlive(true);
        conexion.socket.setSoTimeout(5000);

        OutputStream output = conexion.socket.getOutputStream();
        InputStream input = conexion.socket.getInputStream();

        conexion.writer = new PrintWriter(new OutputStreamWriter(output), true);
        conexion.reader = new BufferedReader(new InputStreamReader(input));

        conexion.conectada = true;
        logger.info("Conexión TCP establecida: {}:{}", host, puerto);
    }

    @Override
    public boolean desconectarImpresora() {
        if (conexionActual != null) {
            try {
                conexionActual.close();
                conexionActual = null;
                logger.info("Desconexión exitosa");
                return true;
            } catch (Exception e) {
                logger.error("Error al desconectar: {}", e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean desconectarImpresoraEspecifica(int printerId) {
        PrinterConnection conexion = conexiones.remove(printerId);
        if (conexion != null) {
            try {
                conexion.close();
                if (conexionActual == conexion) {
                    conexionActual = null;
                }
                logger.info("Desconexión exitosa de impresora [{}]", printerId);
                return true;
            } catch (Exception e) {
                logger.error("Error al desconectar impresora [{}]: {}", printerId, e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    private void desconectarImpresoraInterno(int printerId) {
        PrinterConnection conexion = conexiones.get(printerId);
        if (conexion != null) {
            try {
                conexion.close();
                conexiones.remove(printerId);
                logger.info("Desconexión interna de impresora [{}]", printerId);
            } catch (Exception e) {
                logger.error("Error al desconectar internamente impresora [{}]: {}", printerId,
                        e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean enviarComandoZPL(String comando) {
        if (conexionActual == null || !conexionActual.isValid()) {
            logger.error("Conexión no disponible");
            return false;
        }

        try {
            logger.debug("Enviando comando ZPL");
            conexionActual.writer.println(comando);
            conexionActual.writer.flush();
            logger.info("Comando enviado exitosamente");
            return true;
        } catch (Exception e) {
            logger.error("Error al enviar comando: {}", e.getMessage(), e);
            conexionActual.conectada = false;
            return false;
        }
    }

    /**
     * Envía un comando ZPL a una impresora específica (por ID).
     */
    public boolean enviarComandoZPL(int printerId, String comando) {
        PrinterConnection conexion = obtenerConexionValida(printerId);
        if (conexion == null) {
            logger.error("Conexión no disponible para impresora [{}]", printerId);
            return false;
        }

        try {
            logger.debug("Enviando comando ZPL a impresora [{}]", printerId);
            conexion.writer.println(comando);
            conexion.writer.flush();
            logger.info("Comando enviado exitosamente a impresora [{}]", printerId);
            return true;
        } catch (Exception e) {
            logger.error("Error al enviar comando a impresora [{}]: {}", printerId, e.getMessage(), e);
            conexion.conectada = false;
            return false;
        }
    }

    @Override
    public boolean imprimirEtiqueta(String templateZPL, int cantidad) {
        if (conexionActual == null || !conexionActual.isValid()) {
            logger.error("Conexión no disponible");
            return false;
        }

        try {
            String comando = ajustarQuantity(templateZPL, cantidad);
            logger.info("Imprimiendo {} etiqueta(s)", cantidad);
            conexionActual.writer.println(comando);
            conexionActual.writer.flush();
            Thread.sleep(100);
            logger.info("Etiqueta(s) impresa(s) exitosamente");
            return true;

        } catch (Exception e) {
            logger.error("Error al imprimir: {}", e.getMessage(), e);
            conexionActual.conectada = false;
            return false;
        }
    }

    /**
     * Imprime una etiqueta en una impresora específica (por ID).
     */
    public boolean imprimirEtiqueta(int printerId, String templateZPL, int cantidad) {
        PrinterConnection conexion = obtenerConexionValida(printerId);
        if (conexion == null) {
            logger.error("Conexión no disponible para impresora [{}]", printerId);
            return false;
        }

        try {
            String comando = ajustarQuantity(templateZPL, cantidad);
            logger.info("Imprimiendo {} etiqueta(s) en impresora [{}]", cantidad, printerId);
            conexion.writer.println(comando);
            conexion.writer.flush();
            Thread.sleep(100);
            logger.info("Etiqueta(s) impresa(s) exitosamente en impresora [{}]", printerId);
            return true;

        } catch (Exception e) {
            logger.error("Error al imprimir en impresora [{}]: {}", printerId, e.getMessage(), e);
            conexion.conectada = false;
            return false;
        }
    }

    /**
     * Ajusta la cantidad de etiquetas en el comando ZPL.
     */
    private String ajustarQuantity(String templateZPL, int cantidad) {
        if (cantidad <= 1) {
            return templateZPL;
        }

        if (templateZPL.contains("^PQ")) {
            return templateZPL.replaceAll("\\^PQ\\d+", "^PQ" + cantidad);
        } else if (templateZPL.contains("^XZ")) {
            return templateZPL.replace("^XZ", "^PQ" + cantidad + "^XZ");
        } else {
            return templateZPL + "^PQ" + cantidad;
        }
    }

    @Override
    public String obtenerEstadoImpresora() {
        if (conexionActual == null || !conexionActual.isValid()) {
            return "DESCONECTADA";
        }

        try {
            conexionActual.writer.println("^XA^HH^XZ");
            conexionActual.writer.flush();
            return "CONECTADA";
        } catch (Exception e) {
            logger.error("Error obtener estado: {}", e.getMessage());
            conexionActual.conectada = false;
            return "ERROR";
        }
    }

    /**
     * Obtiene el estado de una impresora específica (por ID).
     */
    public String obtenerEstadoImpresora(int printerId) {
        PrinterConnection conexion = obtenerConexionValida(printerId);
        if (conexion == null) {
            return "DESCONECTADA";
        }

        try {
            conexion.writer.println("^XA^HH^XZ");
            conexion.writer.flush();
            return "CONECTADA";
        } catch (Exception e) {
            logger.error("Error obtener estado impresora [{}]: {}", printerId, e.getMessage());
            conexion.conectada = false;
            return "ERROR";
        }
    }

    @Override
    public boolean estaConectada() {
        return conexionActual != null && conexionActual.isValid();
    }

    @Override
    public boolean estaConectada(int printerId) {
        PrinterConnection conexion = conexiones.get(printerId);
        return conexion != null && conexion.isValid();
    }

    /**
     * Obtiene una conexión válida para una impresora, o null si no está conectada.
     */
    private PrinterConnection obtenerConexionValida(int printerId) {
        PrinterConnection conexion = conexiones.get(printerId);
        return (conexion != null && conexion.isValid()) ? conexion : null;
    }

    /**
     * Obtiene el número de conexiones activas.
     */
    public int obtenerNumeroConexionesActivas() {
        return (int) conexiones.values().stream().filter(PrinterConnection::isValid).count() +
                (estaConectada() ? 1 : 0);
    }

}
