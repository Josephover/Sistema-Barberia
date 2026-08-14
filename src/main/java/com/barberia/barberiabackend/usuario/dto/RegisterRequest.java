package com.barberia.barberiabackend.usuario.dto;

import com.barberia.barberiabackend.usuario.Rol;
import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private Rol rol; // por ahora lo mandamos explícito; luego lo restringimos
}