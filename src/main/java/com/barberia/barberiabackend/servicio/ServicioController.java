package com.barberia.barberiabackend.servicio;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
@RequiredArgsConstructor

public class ServicioController {

    private final ServicioRepository servicioRepository;

    @GetMapping
    public List<Servicio> listarActivos() {
        return servicioRepository.findByActivoTrue();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Servicio crear(@RequestBody Servicio servicio) {
        return servicioRepository.save(servicio);
    }
}