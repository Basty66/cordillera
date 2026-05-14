package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public List<Sucursal> obtenerTodas() {
        return sucursalRepository.findAll();
    }

    public long contarSucursales() {
        return sucursalRepository.count();
    }

    public Sucursal guardarSucursal(Sucursal sucursal) {
        return sucursalRepository.save(sucursal);
    }

    public String generarDatosMasivos(int cantidad) {
        String[] ciudades = {"Santiago", "Valparaíso", "Concepción", "La Serena", "Antofagasta", "Temuco", "Rancagua", "Iquique", "Puerto Montt", "Talca"};
        String[] calles = {"Av. Principal", "Calle Los Leones", "Av. Libertad", "Paseo Ahumada", "Gran Avenida", "Calle Prat", "Av. Brasil", "Calle Caupolicán"};

        List<Sucursal> nuevasSucursales = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Sucursal s = new Sucursal();
            // Generamos datos aleatorios
            String ciudadRandom = ciudades[(int) (Math.random() * ciudades.length)];
            String calleRandom = calles[(int) (Math.random() * calles.length)];
            int numeroRandom = (int)(Math.random() * 9999) + 1;

            String ciudadKey = ciudadRandom.toLowerCase()
                    .replace(" ", "").replace("á","a").replace("é","e")
                    .replace("í","i").replace("ó","o").replace("ú","u");
            s.setNombre("Sucursal " + ciudadRandom + " " + (i + 1));
            s.setCiudad(ciudadRandom);
            s.setDireccion(calleRandom + " " + numeroRandom);
            s.setImagenUrl("https://picsum.photos/seed/" + ciudadKey + (i+1) + "/600/400");

            nuevasSucursales.add(s);
        }

        // Guardamos todas las sucursales de una sola vez
        sucursalRepository.saveAll(nuevasSucursales);
        return "¡Se insertaron " + cantidad + " sucursales falsas con éxito!";
    }
}