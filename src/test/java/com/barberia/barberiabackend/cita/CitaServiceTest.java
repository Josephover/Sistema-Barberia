package com.barberia.barberiabackend.cita;

import com.barberia.barberiabackend.barbero.Barbero;
import com.barberia.barberiabackend.barbero.BarberoRepository;
import com.barberia.barberiabackend.barbero.HorarioDisponible;
import com.barberia.barberiabackend.barbero.HorarioDisponibleRepository;
import com.barberia.barberiabackend.servicio.Servicio;
import com.barberia.barberiabackend.servicio.ServicioRepository;
import com.barberia.barberiabackend.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock private CitaRepository citaRepository;
    @Mock private BarberoRepository barberoRepository;
    @Mock private ServicioRepository servicioRepository;
    @Mock private HorarioDisponibleRepository horarioRepository;

    @InjectMocks
    private CitaService citaService;

    private Barbero barbero;
    private Servicio servicio;
    private Usuario cliente;
    private HorarioDisponible horarioLunes;

    @BeforeEach
    void setUp() {
        barbero = new Barbero();
        barbero.setId(1L);

        servicio = new Servicio();
        servicio.setId(1L);
        servicio.setDuracionMinutos(30);

        cliente = new Usuario();
        cliente.setId(1L);

        horarioLunes = new HorarioDisponible();
        horarioLunes.setBarbero(barbero);
        horarioLunes.setDiaSemana(DayOfWeek.MONDAY);
        horarioLunes.setHoraInicio(LocalTime.of(9, 0));
        horarioLunes.setHoraFin(LocalTime.of(18, 0));
    }

    @Test
    void deberiaCrearCitaCuandoNoHaySolapamientoYEstaEnHorario() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 17, 10, 0); // lunes 10am

        when(barberoRepository.findById(1L)).thenReturn(Optional.of(barbero));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(horarioRepository.findByBarberoIdAndDiaSemana(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(horarioLunes));
        when(citaRepository.findByBarberoIdAndFechaHoraBetweenAndEstadoNot(any(), any(), any(), any()))
                .thenReturn(List.of()); // sin citas previas
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita resultado = citaService.crearCita(cliente, 1L, 1L, fechaHora);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCita.PENDIENTE);
        assertThat(resultado.getFechaHora()).isEqualTo(fechaHora);
    }

    @Test
    void deberiaRechazarCitaCuandoHaySolapamiento() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 17, 10, 15); // se cruza con una existente

        Cita citaExistente = new Cita();
        citaExistente.setFechaHora(LocalDateTime.of(2026, 8, 17, 10, 0));
        citaExistente.setServicio(servicio); // dura 30 min: 10:00-10:30

        when(barberoRepository.findById(1L)).thenReturn(Optional.of(barbero));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(horarioRepository.findByBarberoIdAndDiaSemana(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(horarioLunes));
        when(citaRepository.findByBarberoIdAndFechaHoraBetweenAndEstadoNot(any(), any(), any(), any()))
                .thenReturn(List.of(citaExistente));

        assertThatThrownBy(() -> citaService.crearCita(cliente, 1L, 1L, fechaHora))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya tiene una cita");
    }

    @Test
    void deberiaRechazarCitaFueraDelHorarioLaboral() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 16, 10, 0); // domingo, sin horario cargado

        when(barberoRepository.findById(1L)).thenReturn(Optional.of(barbero));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(horarioRepository.findByBarberoIdAndDiaSemana(1L, DayOfWeek.SUNDAY))
                .thenReturn(List.of()); // no trabaja domingo

        assertThatThrownBy(() -> citaService.crearCita(cliente, 1L, 1L, fechaHora))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no trabaja en ese horario");
    }

    @Test
    void deberiaLanzarErrorCuandoBarberoNoExiste() {
        when(barberoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citaService.crearCita(cliente, 99L, 1L, LocalDateTime.now()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Barbero no encontrado");
    }
}