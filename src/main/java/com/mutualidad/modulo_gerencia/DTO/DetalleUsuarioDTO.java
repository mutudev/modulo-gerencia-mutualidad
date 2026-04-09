package com.mutualidad.modulo_gerencia.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleUsuarioDTO {
    private Integer id;
    private String usuario;
    private Integer empleadoId;
    private String empleadoNombre;
    private String empleadoSoloNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;
    private Integer rolId;
    private String rol;
    private String puesto;
    private Boolean activo;
    private LocalDate fechaNacimiento;
}