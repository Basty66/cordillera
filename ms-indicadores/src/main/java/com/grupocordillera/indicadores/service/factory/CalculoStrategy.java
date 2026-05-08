package com.grupocordillera.indicadores.service.factory;

import java.math.BigDecimal;

public interface CalculoStrategy {
    BigDecimal calcular();
    String getNombre();
    String getUnidad();
}
