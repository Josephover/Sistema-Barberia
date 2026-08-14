package com.barberia.barberiabackend.barbero;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/horarios")
@RequiredArgsConstructor
public class HorarioDisponibleController {

    private final HorarioDisponibleRepository horarioRepository;
    private final BarberoRepository barberoRepository;

    @GetMapping("/barbero/{barberoId}")
    public List<HorarioDisponible> porBarbero(@PathVariable Long barberoId) {
        return horarioRepository.findAll().stream()
                .filter(h -> h.getBarbero().getId().equals(barberoId))
                .toList();
    }

    @PostMapping("/barbero/{barberoId}")
    public HorarioDisponible crear(@PathVariable Long barberoId, @RequestBody HorarioDisponible datos) {
        Barbero barbero = barberoRepository.findById(barberoId)
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado"));

        HorarioDisponible horario = new HorarioDisponible();
        horario.setBarbero(barbero);
        horario.setDiaSemana(datos.getDiaSemana());
        horario.setHoraInicio(datos.getHoraInicio());
        horario.setHoraFin(datos.getHoraFin());

        return horarioRepository.save(horario);
    }
}