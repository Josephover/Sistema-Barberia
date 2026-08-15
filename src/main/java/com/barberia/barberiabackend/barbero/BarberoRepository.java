package com.barberia.barberiabackend.barbero;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BarberoRepository extends JpaRepository<Barbero, Long> {
    Optional<Barbero> findByUsuarioId(Long usuarioId);
}