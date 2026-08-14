package com.barberia.barberiabackend.cita;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CrearCitaRequest {
    private Long barberoId;
    private Long servicioId;
    private LocalDateTime fechaHora;
}