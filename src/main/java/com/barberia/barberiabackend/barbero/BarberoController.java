package com.barberia.barberiabackend.barbero;

import com.barberia.barberiabackend.usuario.Usuario;
import com.barberia.barberiabackend.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/barberos")
@RequiredArgsConstructor
public class BarberoController {

    private final BarberoRepository barberoRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Barbero> listar() {
        return barberoRepository.findAll();
    }

    @PostMapping("/{usuarioId}")
    public Barbero crear(@PathVariable Long usuarioId, @RequestBody Barbero datos) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Barbero barbero = new Barbero();
        barbero.setUsuario(usuario);
        barbero.setEspecialidad(datos.getEspecialidad());
        barbero.setActivo(true);

        return barberoRepository.save(barbero);
    }
}