package com.barberia.barberiabackend.barbero;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.DayOfWeek;
import java.util.List;

public interface HorarioDisponibleRepository extends JpaRepository<HorarioDisponible, Long> {
    List<HorarioDisponible> findByBarberoIdAndDiaSemana(Long barberoId, DayOfWeek diaSemana);
}