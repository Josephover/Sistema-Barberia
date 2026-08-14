package com.barberia.barberiabackend.cita;

import com.barberia.barberiabackend.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;
    private final CitaRepository citaRepository;

    @PostMapping
    public Cita crear(
            @AuthenticationPrincipal Usuario cliente,
            @RequestBody CrearCitaRequest request
    ) {
        return citaService.crearCita(
                cliente,
                request.getBarberoId(),
                request.getServicioId(),
                request.getFechaHora()
        );
    }

    @GetMapping("/mias")
    public Object misCitas(@AuthenticationPrincipal Usuario cliente) {
        return citaRepository.findByClienteId(cliente.getId());
    }
}