package com.barberia.barberiabackend.barbero;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_disponibles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "barbero_id", nullable = false)
    private Barbero barbero;

    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana; // MONDAY, TUESDAY, etc.

    private LocalTime horaInicio;

    private LocalTime horaFin;
}