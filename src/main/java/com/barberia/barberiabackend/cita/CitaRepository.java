package com.barberia.barberiabackend.cita;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByClienteId(Long clienteId);

    List<Cita> findByBarberoId(Long barberoId);

    // Trae las citas activas de un barbero en un rango de tiempo —
    // la usaremos en CitaService para chequear solapamientos
    List<Cita> findByBarberoIdAndFechaHoraBetweenAndEstadoNot(
        Long barberoId, LocalDateTime inicio, LocalDateTime fin, EstadoCita estadoExcluido
    );
}