package com.mutualidad.modulo_gerencia.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleUsuarioDTO {
    private Integer id;
    private String usuario;
    private Integer empleadoId;
    private String empleadoNombre;
    private Integer rolId;
    private String rol;
    private String puesto;
    private Boolean activo;
}