package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "USUARIO")

//SP YA CON LOCKS Y TRANSACCIONES

@NamedStoredProcedureQuery(
        name = "Usuario.pa_ValidarLogin",
        procedureName = "pa_ValidarLogin",
        resultClasses = ModelUsuario.class,
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "Usuario", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Pass", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Resultado", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Rol", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Cajero", type = Integer.class)
        })
@NamedStoredProcedureQuery(
        name = "Socio.pa_CrearUsuario",
        procedureName = "pa_CrearUsuario",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "Nombres", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ApellidoP", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ApellidoM", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "Telefono", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NomUsuario", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CodRol", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CodPuesto", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "Contra", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "FNacimiento", type = LocalDate.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "UsuarioCreador", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Resultado", type = String.class)
        }


)

@NamedStoredProcedureQuery(
        name = "Usuario.pa_GuardarCambiosUsuario",
        procedureName = "pa_GuardarCambiosUsuario",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "Nombre", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ApellidoP", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ApellidoM", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "FechaNacimiento", type = LocalDate.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "codPuesto", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "codRola", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "idUsuario", type = Integer.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "idEmpleado", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "telefono", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "usuarioModificador", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Resultado", type = String.class)
        }
)

public class ModelUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "USUARIO")
    private String usuario;

    @Column(name = "PASS")
    private String pass;

    @Column(name = "ROL_ID")
    private int rol;

    @Column(name = "EMP_ID")
    private int idEmpleado;

    @Column(name = "CAJERO")
    private int cajero;

    @Column(name = "STATUS")
    private boolean status;


}
