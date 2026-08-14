package com.barberia.barberiabackend.barbero;

import com.barberia.barberiabackend.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "barberos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Barbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    private String especialidad; // opcional, ej. "Fades", "Barba"

    private Boolean activo = true;
}