package com.barberia.barberiabackend.servicio;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "servicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // ej. "Corte clásico", "Barba", "Combo"

    @Column(nullable = false)
    private Integer duracionMinutos; // ej. 30, 45, 60

    @Column(nullable = false)
    private Double precio;

    private Boolean activo = true;
}