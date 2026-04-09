package com.mutualidad.modulo_gerencia.DTO.Interfaz;

import java.time.LocalDate;

public interface DetalleUsuarioProjection {
    Integer getId();
    String getUsuario();
    Integer getEmpleado_id();
    String getEmpleado_nombre();
    String getEmpleadoSoloNombre();
    String getApellidoPaterno();
    String getApellidoMaterno();
    String getTelefono();
    Integer getRol_id();
    String getRol();
    String getPuesto();
    Boolean getActivo();
    LocalDate getFechaNacimiento();
}
