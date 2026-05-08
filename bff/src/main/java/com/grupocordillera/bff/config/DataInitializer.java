package com.grupocordillera.bff.config;

import com.grupocordillera.bff.entity.Ticket;
import com.grupocordillera.bff.entity.Usuario;
import com.grupocordillera.bff.repository.TicketRepository;
import com.grupocordillera.bff.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Usuario("admin", encoder.encode("admin123"), "ADMIN", "Administrador", "admin@cordillera.cl"));
                repo.save(new Usuario("vendedor", encoder.encode("ventas123"), "VENDEDOR", "Juan Vendedor", "juan@cordillera.cl"));
                repo.save(new Usuario("bodega", encoder.encode("bodega123"), "BODEGA", "Maria Bodega", "maria@cordillera.cl"));
                repo.save(new Usuario("carla", encoder.encode("carla123"), "VENDEDOR", "Carla Martinez", "carla@cordillera.cl"));
                repo.save(new Usuario("pedro", encoder.encode("pedro123"), "BODEGA", "Pedro Gonzalez", "pedro@cordillera.cl"));
                repo.save(new Usuario("ana", encoder.encode("ana123"), "ADMIN", "Ana Lopez", "ana@cordillera.cl"));
                repo.save(new Usuario("luis", encoder.encode("luis123"), "VENDEDOR", "Luis Ramirez", "luis@cordillera.cl"));
            }
        };
    }

    @Bean
    CommandLineRunner initTickets(TicketRepository repo, UsuarioRepository usuarioRepo) {
        return args -> {
            if (repo.count() == 0) {
                String admin = "admin";
                Ticket t1 = new Ticket("Error en modulo de ventas", "Los totales no cuadran al aplicar descuentos superiores al 30%", Ticket.Prioridad.CRITICA, admin);
                t1.setStatus(Ticket.Status.EN_PROGRESO);
                t1.setAsignadoA("carla");
                repo.save(t1);

                Ticket t2 = new Ticket("Actualizar precios de inventario", "Se necesita recalcular precios de productos importados por fluctuacion cambiaria", Ticket.Prioridad.ALTA, admin);
                t2.setStatus(Ticket.Status.ABIERTO);
                repo.save(t2);

                Ticket t3 = new Ticket("Reporte mensual de ventas", "Generar reporte consolidado de ventas Q1 2026", Ticket.Prioridad.MEDIA, "carla");
                t3.setStatus(Ticket.Status.RESUELTO);
                t3.setAsignadoA("admin");
                repo.save(t3);

                Ticket t4 = new Ticket("Capacitacion nuevo sistema", "Solicitar capacitacion para el equipo de bodega sobre el nuevo modulo de inventarios", Ticket.Prioridad.BAJA, "maria");
                t4.setStatus(Ticket.Status.ABIERTO);
                repo.save(t4);

                Ticket t5 = new Ticket("Fallo en sincronizacion de datos", "Los datos de sucursales no se actualizan correctamente en el dashboard central", Ticket.Prioridad.CRITICA, admin);
                t5.setStatus(Ticket.Status.ABIERTO);
                t5.setAsignadoA("ana");
                repo.save(t5);
            }
        };
    }
}
