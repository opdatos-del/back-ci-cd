package com.example.backend_jovyweb.modules.produccion.util;

/**
 * Clase para calcular productividad basada en producción teórica vs real.
 * 
 * Lógica:
 * - Tiempo efectivo por período: 50 minutos (60 - 10 minutos margen)
 * - Producción teórica = kgCofre × velocidadMáquina × 50
 * - Productividad = (Producción Real / Producción Teórica) × 100
 * - Minutos perdidos/ganados: basados en 60 minutos de referencia
 * * Tiempo para producir real = (Producción Real × 60) / Producción Teórica
 * * Minutos perdidos/ganados = 60 - tiempo para producir real
 * - Si resultado positivo: minutos perdidos (se guardan como negativos en BD)
 * - Si resultado negativo: minutos a favor (se guardan como positivos en BD)
 */
public class ProductividadCalculadora {

    private static final int MINUTOS_EFECTIVOS_HORA = 50;
    private static final double FACTOR_REDONDEO = 100.0;

    /**
     * Calcula la producción teórica basada en kg por cofre y velocidad de la
     * máquina.
     * 
     * Fórmula: kgCofre × velocidadMáquina × 50
     * 
     * @param kgPorCofre       Kilogramos por cofre
     * @param velocidadMaquina Velocidad de la máquina
     * @return Producción teórica calculada
     */
    public static int calcularProduccionTeorica(int kgPorCofre, int velocidadMaquina) {
        return kgPorCofre * velocidadMaquina * MINUTOS_EFECTIVOS_HORA;
    }

    /**
     * Calcula la productividad en porcentaje
     * 
     * @param produccionReal    Cantidad producida realmente
     * @param produccionTeorica Cantidad que debería producirse teóricamente
     * @return Porcentaje de productividad redondeado a 2 decimales
     */
    public static double calcularProductividad(int produccionReal, int produccionTeorica) {
        if (produccionTeorica == 0) {
            return 0;
        }
        double productividad = ((double) produccionReal / produccionTeorica) * 100;
        return Math.round(productividad * FACTOR_REDONDEO) / FACTOR_REDONDEO;
    }

    /**
     * Calcula los minutos a favor o en contra basados en 60 minutos de referencia.
     * 
     * Lógica:
     * - Tiempo para producir real = (produccionReal × 60) / produccionTeorica
     * - Minutos perdidos/ganados = 60 - tiempo para producir real
     * 
     * @param produccionReal    Cantidad producida realmente
     * @param produccionTeorica Cantidad que debería producirse teóricamente
     * @return Minutos perdidos (positivo) o minutos a favor (negativo)
     */
    public static double calcularMinutosVariacion(int produccionReal, int produccionTeorica) {
        if (produccionTeorica == 0) {
            return 0;
        }

        // Calcular tiempo en minutos para producir lo real
        double tiempoParaProducirReal = (produccionReal * 60.0) / produccionTeorica;

        // Minutos perdidos = 60 - tiempo que tardó
        double minutosVariacion = 60 - tiempoParaProducirReal;

        return Math.round(minutosVariacion * FACTOR_REDONDEO) / FACTOR_REDONDEO;
    }

    /**
     * Valida y retorna los minutos como valor absoluto con indicador
     * 
     * @param produccionReal    Cantidad producida realmente
     * @param produccionTeorica Cantidad que debería producirse teóricamente
     * @return Objeto con minutos y tipo (a favor o perdidos)
     */
    public static MinutosVariacion calcularMinutosDetallado(int produccionReal, int produccionTeorica) {
        double minutos = calcularMinutosVariacion(produccionReal, produccionTeorica);

        if (minutos > 0) {
            return new MinutosVariacion(minutos, "PERDIDOS");
        } else if (minutos < 0) {
            return new MinutosVariacion(Math.abs(minutos), "A FAVOR");
        } else {
            return new MinutosVariacion(0, "SIN VARIACIÓN");
        }
    }

    /**
     * Clase interna para retornar minutos con su clasificación
     */
    public static class MinutosVariacion {
        private double minutos;
        private String tipo;

        public MinutosVariacion(double minutos, String tipo) {
            this.minutos = minutos;
            this.tipo = tipo;
        }

        public double getMinutos() {
            return minutos;
        }

        public String getTipo() {
            return tipo;
        }

        public boolean esAFavor() {
            return "A FAVOR".equals(tipo);
        }

        public boolean esPerdido() {
            return "PERDIDOS".equals(tipo);
        }

        @Override
        public String toString() {
            return minutos + " min - " + tipo;
        }
    }
}
