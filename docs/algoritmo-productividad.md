# Algoritmo de Cálculo de Productividad

## Resumen

El sistema calcula automáticamente la **productividad** y los **minutos a favor/en contra** comparando la producción teórica con la producción real.

## Parámetros Base
- **Tiempo efectivo por hora**: 50 minutos (60 - 10 minutos de margen)
- **Producción Teórica (PROD_prodTeo)**: Lo que debería producir en 50 minutos
- **Producción Real (PROD_prodReal)**: Lo que realmente produce en 50 minutos

## Fórmulas

### 1. Productividad (%)
```
Productividad = (Producción Real / Producción Teórica) × 100
```

**Ejemplos**:
- Teórica: 3360 | Real: 4400 → Productividad: (4400/3360) × 100 = **131%**
- Teórica: 3360 | Real: 2400 → Productividad: (2400/3360) × 100 = **71%**

### 2. Minutos a Favor o Perdidos
```
Minutos Variación = 50 × (1 - Producción Teórica / Producción Real)
```

**Si resultado > 0**: Minutos a favor (adelanto)
**Si resultado < 0**: Minutos perdidos (atraso)
**Si resultado = 0**: Sin variación

**Ejemplos**:
- Teórica: 3360 | Real: 4400 → Minutos: 50 × (1 - 3360/4400) = 50 × 0.2364 = **+18.57 min a FAVOR**
- Teórica: 3360 | Real: 2400 → Minutos: 50 × (1 - 3360/2400) = 50 × (-0.4) = **-20 min = 20 min PERDIDOS**
