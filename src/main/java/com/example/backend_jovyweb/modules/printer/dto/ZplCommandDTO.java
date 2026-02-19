package com.example.backend_jovyweb.modules.printer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DTO para enviar comandos ZPL personalizados a una impresora.
 */
public class ZplCommandDTO {

    @NotNull(message = "El ID de la impresora es requerido")
    @Positive(message = "El ID de la impresora debe ser positivo")
    @JsonProperty("PRI_IdImp")
    private Integer printerId;

    @NotBlank(message = "El comando ZPL es requerido")
    @JsonProperty("PRI_Comando")
    private String comando;

    public ZplCommandDTO() {
    }

    public ZplCommandDTO(Integer printerId, String comando) {
        this.printerId = printerId;
        this.comando = comando;
    }

    public Integer getPrinterId() {
        return printerId;
    }

    public void setPrinterId(Integer printerId) {
        this.printerId = printerId;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    @Override
    public String toString() {
        return "ZplCommandDTO{" +
                "printerId=" + printerId +
                ", comando='" + comando + '\'' +
                '}';
    }
}
