package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CONF_MODULO")
@NamedStoredProcedureQuery(
        name = "ConfModulo.pa_ModificarPermisos",
        procedureName = "pa_ModificarPermisos",
        resultClasses = ModelConfModulo.class,
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "datos_permisos", type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "usuario_id", type = Integer.class ),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Resultado", type = String.class )
        })
public class ModelConfModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "USUARIO_ID")
    private  int usuarioId;

    @Column(name = "MODULO_ID")
    private int moduloId;

}
