package com.grupocordillera.bff.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TicketClassificationService {

    private static final Map<String, String[]> PALABRAS_CLAVE = Map.of(
        "TÉCNICO", new String[]{"error", "falla", "bug", "sistema", "pantalla", "crash", "lento", "no funciona",
                                 "clave", "acceso", "login", "contraseña", "servidor", "base de datos", "bd", "caido"},
        "FACTURACIÓN", new String[]{"factura", "boleta", "pago", "cobro", "precio", "costo", "descuento",
                                    "iva", "neto", "bruto", "abono", "deuda", "saldo", "facturacion"},
        "RECLAMO", new String[]{"reclamo", "queja", "devolucion", "cambio", "reembolso", "garantia",
                                "defecto", "mal estado", "roto", "dañado", "insatisfecho", "problema"},
        "CONSULTA", new String[]{"consulta", "duda", "informacion", "como", "cuando", "donde", "que",
                                 "requisito", "proceso", "horario", "disponible", "stock"}
    );

    public String clasificar(String titulo, String descripcion) {
        String texto = (titulo + " " + (descripcion != null ? descripcion : "")).toLowerCase();

        int maxPuntaje = 0;
        String mejorCategoria = "CONSULTA";

        for (Map.Entry<String, String[]> entry : PALABRAS_CLAVE.entrySet()) {
            String categoria = entry.getKey();
            int puntaje = 0;
            for (String palabra : entry.getValue()) {
                int idx = 0;
                while ((idx = texto.indexOf(palabra, idx)) != -1) {
                    puntaje++;
                    idx += palabra.length();
                }
            }
            if (puntaje > maxPuntaje) {
                maxPuntaje = puntaje;
                mejorCategoria = categoria;
            }
        }
        return mejorCategoria;
    }

    public String[] obtenerCategorias() {
        return PALABRAS_CLAVE.keySet().toArray(new String[0]);
    }
}
