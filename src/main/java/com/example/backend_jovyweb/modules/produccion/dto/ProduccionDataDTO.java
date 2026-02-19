package com.example.backend_jovyweb.modules.produccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO que contiene la respuesta completa del SP PROD_GDataPAPMPRPER.
 * Agrupa áreas/máquinas, empleados activos y órdenes de productividad en
 * abierto.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProduccionDataDTO {

    /** Lista de áreas con máquinas (PROD_ARMACH) */
    @JsonProperty("PROD_ARMACH")
    private List<AreaMaquinaDTO> areasMachines;

    /** Lista de empleados activos (PROD_ACTEMP) */
    @JsonProperty("PROD_ACTEMP")
    private List<EmpleadosActivosDTO> activeEmployees;

    /** Lista de órdenes de productividad abiertas (PROD_OP) */
    @JsonProperty("PROD_OP")
    private List<OrdenProductividadDTO> openOrders;

    public ProduccionDataDTO() {
    }

    public ProduccionDataDTO(List<AreaMaquinaDTO> areasMachines,
            List<EmpleadosActivosDTO> activeEmployees,
            List<OrdenProductividadDTO> openOrders) {
        this.areasMachines = areasMachines;
        this.activeEmployees = activeEmployees;
        this.openOrders = openOrders;
    }

    public List<AreaMaquinaDTO> getAreasMachines() {
        return areasMachines;
    }

    public void setAreasMachines(List<AreaMaquinaDTO> areasMachines) {
        this.areasMachines = areasMachines;
    }

    public List<EmpleadosActivosDTO> getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(List<EmpleadosActivosDTO> activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public List<OrdenProductividadDTO> getOpenOrders() {
        return openOrders;
    }

    public void setOpenOrders(List<OrdenProductividadDTO> openOrders) {
        this.openOrders = openOrders;
    }

    @Override
    public String toString() {
        return "ProduccionDataDTO{" +
                "areasMachines=" + (areasMachines != null ? areasMachines.size() : 0) +
                ", activeEmployees=" + (activeEmployees != null ? activeEmployees.size() : 0) +
                ", openOrders=" + (openOrders != null ? openOrders.size() : 0) +
                '}';
    }
}
