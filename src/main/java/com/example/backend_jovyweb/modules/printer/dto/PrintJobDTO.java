package com.example.backend_jovyweb.modules.printer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de impresión de etiquetas ZPL.
 */
public class PrintJobDTO {

    @NotNull(message = "El ID de la impresora es requerido")
    @Positive(message = "El ID de la impresora debe ser positivo")
    @JsonProperty("PRI_IdImp")
    private Integer printerId;

    @NotBlank(message = "La plantilla ZPL es requerida")
    @JsonProperty("PRI_PlantillaZPL")
    private String templateZPL;

    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @JsonProperty("PRI_Cantidad")
    private Integer cantidad = 1;

    public PrintJobDTO() {
    }

    public PrintJobDTO(Integer printerId, String templateZPL, Integer cantidad) {
        this.printerId = printerId;
        this.templateZPL = templateZPL;
        this.cantidad = cantidad;
    }

    public Integer getPrinterId() {
        return printerId;
    }

    public void setPrinterId(Integer printerId) {
        this.printerId = printerId;
    }

    public String getTemplateZPL() {
        return templateZPL;
    }

    public void setTemplateZPL(String templateZPL) {
        this.templateZPL = templateZPL;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "PrintJobDTO{" +
                "printerId=" + printerId +
                ", templateZPL='" + templateZPL + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}
