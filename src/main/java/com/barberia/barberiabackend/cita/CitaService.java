package com.barberia.barberiabackend.cita;

import com.barberia.barberiabackend.barbero.Barbero;
import com.barberia.barberiabackend.barbero.BarberoRepository;
import com.barberia.barberiabackend.barbero.HorarioDisponible;
import com.barberia.barberiabackend.barbero.HorarioDisponibleRepository;
import com.barberia.barberiabackend.servicio.Servicio;
import com.barberia.barberiabackend.servicio.ServicioRepository;
import com.barberia.barberiabackend.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final HorarioDisponibleRepository horarioRepository;

    @Transactional
    public Cita crearCita(Usuario cliente, Long barberoId, Long servicioId, LocalDateTime fechaHora) {
        Barbero barbero = barberoRepository.findById(barberoId)
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado"));

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        validarDentroDeHorarioLaboral(barbero, fechaHora, servicio.getDuracionMinutos());
        validarSinSolapamiento(barbero, fechaHora, servicio.getDuracionMinutos());

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setBarbero(barbero);
        cita.setServicio(servicio);
        cita.setFechaHora(fechaHora);
        cita.setEstado(EstadoCita.PENDIENTE);

        return citaRepository.save(cita);
    }

    private void validarDentroDeHorarioLaboral(Barbero barbero, LocalDateTime fechaHora, int duracionMinutos) {
        List<HorarioDisponible> horarios = horarioRepository
                .findByBarberoIdAndDiaSemana(barbero.getId(), fechaHora.getDayOfWeek());

        LocalTime horaInicioCita = fechaHora.toLocalTime();
        LocalTime horaFinCita = fechaHora.plusMinutes(duracionMinutos).toLocalTime();

        boolean dentroDeAlgunHorario = horarios.stream().anyMatch(h ->
                !horaInicioCita.isBefore(h.getHoraInicio()) && !horaFinCita.isAfter(h.getHoraFin())
        );

        if (!dentroDeAlgunHorario) {
            throw new IllegalArgumentException(
                "El barbero no trabaja en ese horario (" + fechaHora.getDayOfWeek() + " " + horaInicioCita + ")"
            );
        }
    }

    private void validarSinSolapamiento(Barbero barbero, LocalDateTime fechaHora, int duracionMinutos) {
        LocalDateTime finNuevaCita = fechaHora.plusMinutes(duracionMinutos);

        // Traemos las citas activas del barbero en una ventana amplia alrededor del horario pedido
        List<Cita> citasDelDia = citaRepository.findByBarberoIdAndFechaHoraBetweenAndEstadoNot(
                barbero.getId(),
                fechaHora.toLocalDate().atStartOfDay(),
                fechaHora.toLocalDate().atTime(23, 59, 59),
                EstadoCita.CANCELADA
        );

        boolean haySolapamiento = citasDelDia.stream().anyMatch(citaExistente -> {
            LocalDateTime inicioExistente = citaExistente.getFechaHora();
            LocalDateTime finExistente = inicioExistente.plusMinutes(
                    citaExistente.getServicio().getDuracionMinutos()
            );
            // Dos rangos se solapan si uno empieza antes de que el otro termine, y viceversa
            return fechaHora.isBefore(finExistente) && inicioExistente.isBefore(finNuevaCita);
        });

        if (haySolapamiento) {
            throw new IllegalArgumentException("El barbero ya tiene una cita en ese horario");
        }
    }
}