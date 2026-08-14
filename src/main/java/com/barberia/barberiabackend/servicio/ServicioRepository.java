package com.barberia.barberiabackend.servicio;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByActivoTrue();
}