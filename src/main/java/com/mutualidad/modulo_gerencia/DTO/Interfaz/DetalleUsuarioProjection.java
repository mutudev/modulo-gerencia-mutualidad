package com.mutualidad.modulo_gerencia.DTO.Interfaz;

public interface DetalleUsuarioProjection {
    Integer getId();
    String getUsuario();
    Integer getEmpleado_id();
    String getEmpleado_nombre();
    Integer getRol_id();
    String getRol();
    String getPuesto();
    Boolean getActivo();
}
