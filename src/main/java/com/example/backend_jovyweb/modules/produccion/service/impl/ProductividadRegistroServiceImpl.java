package com.example.backend_jovyweb.modules.produccion.service.impl;

import com.example.backend_jovyweb.modules.produccion.dto.ProductividadRegistroDTO;
import com.example.backend_jovyweb.modules.produccion.dto.RegistroProductividadXmlDTO;
import com.example.backend_jovyweb.modules.produccion.dto.ProductividadRegistroDetalleDTO;
import com.example.backend_jovyweb.modules.produccion.model.ProductividadRegistro;
import com.example.backend_jovyweb.modules.produccion.repository.ProductividadRegistroRepository;
import com.example.backend_jovyweb.modules.produccion.service.ProductividadRegistroService;
import com.example.backend_jovyweb.modules.produccion.util.ProductividadCalculadora;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de implementación para gestionar registros de productividad.
 * Proporciona funcionalidades para crear, consultar y obtener detalles de
 * registros de productividad.
 * También calcula automáticamente la productividad basada en producción teórica
 * vs real.
 */
@Service
public class ProductividadRegistroServiceImpl implements ProductividadRegistroService {

    private final ProductividadRegistroRepository repository;

    public ProductividadRegistroServiceImpl(ProductividadRegistroRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea un nuevo registro de productividad.
     * Calcula automáticamente la productividad y minutos basados en producción
     * teórica vs real.
     * 
     * @param registroDTO DTO del registro a crear con los datos de entrada
     * @return DTO del registro creado con ID asignado
     */
    @Override
    public ProductividadRegistroDTO crearRegistro(ProductividadRegistroDTO registroDTO) {
        // Calcular productividad y minutos basados en producción teórica vs real
        calcularProductividadYMinutos(registroDTO);

        ProductividadRegistro registro = mapearDTOAModelo(registroDTO);
        ProductividadRegistro guardado = repository.crearRegistro(registro);
        return mapearModeloADTO(guardado);
    }

    /**
     * Crea un nuevo registro de productividad a partir de XML.
     * Convierte el XML (formato ProR002, ProR003, etc.) al DTO y lo procesa.
     * 
     * /*
     * MÉTODO COMENTADO (Para uso interno - NO UTILIZAR)
     * ================================================
     * Este método es deprecated. Usar
     * crearRegistroDesdeXmlDto(RegistroProductividadXmlDTO)
     * Ya que Spring deserializa el XML directamente a través de Jackson.
     * 
     * @Override
     *           public ProductividadRegistroDTO crearRegistroDesdeXml(String
     *           xmlBody) {
     *           // Métod deprecated - usar crearRegistroDesdeXmlDto
     *           }
     */

    /**
     * Obtiene la lista de todos los registros de productividad guardados.
     * 
     * @return Lista de DTOs de registros de productividad
     */
    @Override
    public List<ProductividadRegistroDTO> obtenerRegistros() {
        List<ProductividadRegistro> registros = repository.obtenerRegistros();
        List<ProductividadRegistroDTO> registrosDTO = new ArrayList<>();
        for (ProductividadRegistro registro : registros) {
            registrosDTO.add(mapearModeloADTO(registro));
        }
        return registrosDTO;
    }

    /**
     * Convierte un modelo de ProductividadRegistro a su DTO.
     * 
     * @param modelo Modelo de ProductividadRegistro
     * @return DTO equivalente con todos los datos mapeados
     */
    private ProductividadRegistroDTO mapearModeloADTO(ProductividadRegistro modelo) {
        ProductividadRegistroDTO dto = new ProductividadRegistroDTO();
        dto.setId(modelo.getProR001());
        dto.setNumeroOrden(modelo.getProR002());
        dto.setFechaInicio(modelo.getProR003());
        dto.setHoraInicio(modelo.getProR004());
        dto.setCodigoSupervisor(modelo.getProR005());
        dto.setNombreSupervisor(modelo.getProR006());
        dto.setCantidadPlanificada(modelo.getProR007());
        dto.setCodigoProducto(modelo.getProR008());
        dto.setDescripcionProducto(modelo.getProR009());
        dto.setNumeroLote(modelo.getProR010());
        dto.setAreaId(modelo.getProA001());
        dto.setMaquinaId(modelo.getProM001());
        dto.setStatus(modelo.getProR011());
        dto.setKgPorCofre(modelo.getProR012());
        dto.setVelocidadMaquina(modelo.getProR013());
        dto.setProduccionTeorica(modelo.getProR014());
        dto.setProduccionReal(modelo.getProR015());
        dto.setPorcentajeRecibido(modelo.getProR018());
        dto.setMinutosPerdidos(modelo.getProR019());
        dto.setObservaciones(modelo.getProR017());
        return dto;
    }

    /**
     * Convierte un DTO de ProductividadRegistro a su modelo.
     * 
     * @param dto DTO de ProductividadRegistro
     * @return Modelo equivalente con todos los datos mapeados
     */
    private ProductividadRegistro mapearDTOAModelo(ProductividadRegistroDTO dto) {
        ProductividadRegistro modelo = new ProductividadRegistro();
        modelo.setProR002(dto.getNumeroOrden());
        modelo.setProR003(dto.getFechaInicio());
        modelo.setProR004(dto.getHoraInicio());
        modelo.setProR005(dto.getCodigoSupervisor());
        modelo.setProR006(dto.getNombreSupervisor());
        modelo.setProR007(dto.getCantidadPlanificada());
        modelo.setProR008(dto.getCodigoProducto());
        modelo.setProR009(dto.getDescripcionProducto());
        modelo.setProR010(dto.getNumeroLote());
        modelo.setProA001(dto.getAreaId());
        modelo.setProM001(dto.getMaquinaId());
        modelo.setProR011(dto.getStatus());
        modelo.setProR012(dto.getKgPorCofre());
        modelo.setProR013(dto.getVelocidadMaquina());
        modelo.setProR014(dto.getProduccionTeorica());
        modelo.setProR015(dto.getProduccionReal());
        modelo.setProR018(dto.getPorcentajeRecibido());
        modelo.setProR019(dto.getMinutosPerdidos());
        modelo.setProR017(dto.getObservaciones());
        return modelo;
    }

    /**
     * Calcula automáticamente la producción teórica, productividad (porcentaje) y
     * minutos (perdidos o a favor).
     * 
     * Fórmula de producción teórica: kgCofre × velocidadMáquina × 50
     * 
     * Los minutos se guardan: negativos = perdidos, positivos = a favor.
     * 
     * @param registroDTO DTO del registro a procesar (se modifica in-place)
     */
    private void calcularProductividadYMinutos(ProductividadRegistroDTO registroDTO) {
        int kgPorCofre = registroDTO.getKgPorCofre();
        int velocidadMaquina = registroDTO.getVelocidadMaquina();
        int produccionReal = registroDTO.getProduccionReal();

        // Calcular producción teórica automáticamente
        int produccionTeorica = ProductividadCalculadora.calcularProduccionTeorica(
                kgPorCofre, velocidadMaquina);
        registroDTO.setProduccionTeorica(produccionTeorica);

        if (produccionTeorica > 0 && produccionReal >= 0) {
            // Calcular porcentaje de productividad
            double productividad = ProductividadCalculadora.calcularProductividad(
                    produccionReal, produccionTeorica);
            registroDTO.setPorcentajeRecibido((int) Math.round(productividad));

            // Calcular minutos a favor o en contra
            ProductividadCalculadora.MinutosVariacion minutosVariacion = ProductividadCalculadora
                    .calcularMinutosDetallado(
                            produccionReal, produccionTeorica);

            // Almacenar minutos: negativos = perdidos, positivos = a favor
            int minutosValor = (int) Math.round(minutosVariacion.getMinutos());
            if (minutosVariacion.esPerdido()) {
                // Minutos perdidos se guardan como negativos
                registroDTO.setMinutosPerdidos(-minutosValor);
            } else {
                // Minutos a favor se guardan como positivos
                registroDTO.setMinutosPerdidos(minutosValor);
            }
        }
    }

    /**
     * Crea un nuevo registro de productividad a partir de DTO XML deserializado.
     * Convierte el DTO deserializado (RowData) con todos los campos como strings
     * a ProductividadRegistroDTO realizando las conversiones de tipos necesarias.
     * 
     * @param xmlDto DTO parseado del XML con campos ProR002-ProR019 como strings
     * @return DTO del registro creado
     */
    @Override
    public ProductividadRegistroDTO crearRegistroDesdeXmlDto(RegistroProductividadXmlDTO xmlDto) {
        try {
            RegistroProductividadXmlDTO.RowData row = xmlDto.getRow();

            // Convertir de RowData (todos strings) a ProductividadRegistroDTO
            ProductividadRegistroDTO registroDTO = new ProductividadRegistroDTO();

            // Convertir ProR002 (Number de orden)
            registroDTO.setNumeroOrden(row.getProR002() != null && !row.getProR002().isEmpty()
                    ? Integer.parseInt(row.getProR002())
                    : 0);

            // Parsear ProR003 (fecha)
            if (row.getProR003() != null && !row.getProR003().isEmpty()) {
                registroDTO.setFechaInicio(LocalDate.parse(row.getProR003()));
            }

            // Parsear ProR004 (hora)
            if (row.getProR004() != null && !row.getProR004().isEmpty()) {
                registroDTO.setHoraInicio(LocalTime.parse(row.getProR004()));
            }

            // Convertir ProR005 (Código Supervisor)
            registroDTO.setCodigoSupervisor(row.getProR005() != null && !row.getProR005().isEmpty()
                    ? Integer.parseInt(row.getProR005())
                    : 0);

            registroDTO.setNombreSupervisor(row.getProR006());
            registroDTO.setCantidadPlanificada(row.getProR007());

            // Convertir ProR008 (Código Producto)
            registroDTO.setCodigoProducto(row.getProR008() != null && !row.getProR008().isEmpty()
                    ? Integer.parseInt(row.getProR008())
                    : 0);

            registroDTO.setDescripcionProducto(row.getProR009());
            registroDTO.setNumeroLote(row.getProR010());

            // Convertir ProA001 (Area ID)
            registroDTO.setAreaId(row.getProA001() != null && !row.getProA001().isEmpty()
                    ? Integer.parseInt(row.getProA001())
                    : 0);

            // Convertir ProM001 (Máquina ID)
            registroDTO.setMaquinaId(row.getProM001() != null && !row.getProM001().isEmpty()
                    ? Integer.parseInt(row.getProM001())
                    : 0);

            // Convertir ProR011 (Status)
            registroDTO.setStatus(row.getProR011() != null && !row.getProR011().isEmpty()
                    ? Integer.parseInt(row.getProR011())
                    : 0);

            // Convertir ProR012 (Kg por cofre) - es un Double en XML
            registroDTO.setKgPorCofre(row.getProR012() != null && !row.getProR012().isEmpty()
                    ? (int) Double.parseDouble(row.getProR012())
                    : 0);

            // Convertir ProR013 (Velocidad Máquina)
            registroDTO.setVelocidadMaquina(row.getProR013() != null && !row.getProR013().isEmpty()
                    ? Integer.parseInt(row.getProR013())
                    : 0);

            // Convertir ProR014 (Producción Teórica)
            registroDTO.setProduccionTeorica(row.getProR014() != null && !row.getProR014().isEmpty()
                    ? Integer.parseInt(row.getProR014())
                    : 0);

            // Convertir ProR015 (Producción Real)
            registroDTO.setProduccionReal(row.getProR015() != null && !row.getProR015().isEmpty()
                    ? Integer.parseInt(row.getProR015())
                    : 0);

            // Convertir ProR018 (Porcentaje Recibido)
            registroDTO.setPorcentajeRecibido(row.getProR018() != null && !row.getProR018().isEmpty()
                    ? Integer.parseInt(row.getProR018())
                    : 0);

            // Convertir ProR019 (Minutos Perdidos)
            registroDTO.setMinutosPerdidos(row.getProR019() != null && !row.getProR019().isEmpty()
                    ? Integer.parseInt(row.getProR019())
                    : 0);

            registroDTO.setObservaciones(row.getProR017());

            // Procesar como registro normal (calcula productividad automáticamente)
            return crearRegistro(registroDTO);

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar XML: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el detalle de una orden por su número de orden.
     * Busca el primer registro asociado a ese número de orden.
     * 
     * @param numeroOrden Número de la orden de producción (ProR002)
     * @return Optional con el DTO detallado o vacío si no existe
     */
    @Override
    public Optional<ProductividadRegistroDetalleDTO> obtenerDetalleRegistroPorNumeroOrden(int numeroOrden) {
        List<ProductividadRegistro> registros = repository.obtenerRegistrosPorNumeroOrden(numeroOrden);

        if (registros.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(construirDetalleRegistro(registros.get(0)));
    }

    @Override
    public List<ProductividadRegistroDetalleDTO> obtenerDetallesRegistrosPorNumeroOrden(int numeroOrden) {
        List<ProductividadRegistro> registros = repository.obtenerRegistrosPorNumeroOrden(numeroOrden);
        List<ProductividadRegistroDetalleDTO> detalles = new ArrayList<>();

        for (ProductividadRegistro reg : registros) {
            detalles.add(construirDetalleRegistro(reg));
        }

        return detalles;
    }

    /**
     * Construye el DTO detallado a partir de un modelo de ProductividadRegistro.
     * Encapsula la lógica común de mapeo y cálculo de métricas.
     *
     * @param reg Modelo del registro de productividad
     * @return DTO detallado con todas las métricas calculadas
     */
    private ProductividadRegistroDetalleDTO construirDetalleRegistro(ProductividadRegistro reg) {
        ProductividadRegistroDetalleDTO detalle = new ProductividadRegistroDetalleDTO();

        detalle.setRegistroId(reg.getProR001());
        detalle.setNumeroOrden(reg.getProR002());
        detalle.setProductoDescripcion(reg.getProR009());
        detalle.setFecha(reg.getProR003());
        detalle.setHora(reg.getProR004());

        // Calcular productividad
        double productividad = calculateProductivityPercentage(
                reg.getProR015(), reg.getProR014());
        detalle.setProductividad(productividad);

        // Calcular minutos perdidos/ganados
        calcularMinutosVariacion(reg, detalle);

        detalle.setSupervisorId(reg.getProR005());
        detalle.setSupervisorNombre(reg.getProR006());
        detalle.setKgProcesados(reg.getProR012() != 0 ? reg.getProR012() : 0);
        detalle.setVelocidad(reg.getProR013());
        detalle.setProduccionTeorica(reg.getProR014());
        detalle.setProduccionReal(reg.getProR015());

        detalle.setAreaId(reg.getProA001());
        com.example.backend_jovyweb.modules.produccion.model.Area area = repository
                .obtenerAreaPorId(reg.getProA001());
        if (area != null) {
            detalle.setAreaNombre(area.getProA002());
        }

        detalle.setMaquinaId(reg.getProM001());
        com.example.backend_jovyweb.modules.produccion.model.Maquina maquina = repository
                .obtenerMaquinaPorId(reg.getProM001());
        if (maquina != null) {
            detalle.setMaquinaNombre(maquina.getProM002());
        }

        detalle.setNumeroLote(reg.getProR010());
        detalle.setCantidadPlanificada(reg.getProR007());
        detalle.setObservaciones(reg.getProR017());

        return detalle;
    }

    /**
     * Calcula el porcentaje de productividad.
     * Fórmula: (produccionReal / produccionTeorica) * 100
     *
     * @param produccionReal    Producción real alcanzada
     * @param produccionTeorica Producción teórica esperada
     * @return Porcentaje de productividad (0-100)
     */
    private double calculateProductivityPercentage(int produccionReal, int produccionTeorica) {
        if (produccionTeorica <= 0) {
            return 0;
        }
        return (produccionReal * 100.0) / produccionTeorica;
    }

    /**
     * Calcula y asigna los minutos perdidos o ganados en un registro detallado.
     *
     * @param registro Modelo del registro de productividad
     * @param detalle  DTO detallado donde se asignarán los valores
     */
    private void calcularMinutosVariacion(ProductividadRegistro registro,
            ProductividadRegistroDetalleDTO detalle) {
        int produccionTeorica = registro.getProR014();
        int produccionReal = registro.getProR015();

        if (produccionTeorica > 0) {
            // Usar la calculadora para obtener minutos detallados
            ProductividadCalculadora.MinutosVariacion minutosVar = ProductividadCalculadora.calcularMinutosDetallado(
                    produccionReal, produccionTeorica);

            if (minutosVar.esPerdido()) {
                detalle.setMinutosPerdidos(minutosVar.getMinutos());
                detalle.setMinutosGanados(0);
            } else {
                detalle.setMinutosPerdidos(0);
                detalle.setMinutosGanados(minutosVar.getMinutos());
            }
        }
    }

}
