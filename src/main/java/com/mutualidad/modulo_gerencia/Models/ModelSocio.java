package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "SOCIO")

//SP YA CON LOCKS Y TRANSACCIONES

@NamedStoredProcedureQuery(
        name = "Socio.pa_InsertarSocio",
        procedureName = "pa_InsertarSocio",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NOMBRES", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "APELLIDO_P", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "APELLIDO_M", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "FECHA_NACIMIENTO", type = LocalDate.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CURP", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CAT_EMP_ID", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "DIRECCION", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CAT_ESTADO_ID", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CAT_MUNICIPIO_ID", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CAT_TIPO_ID", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "RFC", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ESTADO_CIVIL_ID", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TELEFONO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "USUARIO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "GENERO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "RESULTADO", type = String.class)
        }
)
@NamedStoredProcedureQuery(
        name = "Socio.pa_EditarSocio",
        procedureName = "pa_EditarSocio",
        parameters = {

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NUM_SOCIO", type = Integer.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NOMBRES", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "APELLIDO_P", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "APELLIDO_M", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "FECHA_NACIMIENTO", type = LocalDate.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CURP", type = String.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CAT_EMP_ID", type = Integer.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "DIRECCION", type = String.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CAT_ESTADO_ID", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "CAT_MUNICIPIO_ID", type = Integer.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "RFC", type = String.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ESTADO_CIVIL_ID", type = Integer.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TELEFONO", type = String.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "GENERO", type = String.class),

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "USUARIO", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "TIPO_ID", type = Integer.class),


                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "RESULTADO", type = String.class)
        }
)

@NamedStoredProcedureQuery(
        name = "Socio.pa_InsertarBeneficiario",
        procedureName = "pa_InsertarBeneficiario",
        resultClasses = ModelSocio.class,
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "datos_beneficiarios", type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "actualizar", type = Integer.class ),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Resultado", type = String.class )
        })
public class ModelSocio {

    @Id
    @Column(name = "NUM_SOCIO")
    private int numSocio;

    @Column(name = "EMPRESA_COD", length = 4, nullable = false)
    private String empresaCod;

    @Column(name = "NOMBRES", length = 25, nullable = false)
    private String nombres;

    @Column(name = "APELLIDO_P", length = 25, nullable = false)
    private String apellidoP;

    @Column(name = "APELLIDO_M", length = 25)
    private String apellidoM;

    @Column(name = "FECHA_NACIMIENTO", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "CURP", length = 21, nullable = false)
    private String curp;

    @Column(name = "CAT_EMP_ID")
    private int catEmpId;

    @Column(name = "DIRECCION", length = 50)
    private String direccion;

    @Column(name = "CAT_ESTADO_ID")
    private int catEstadoId;

    @Column(name = "CAT_MUNICIPIO_ID")
    private int catMunicipioId;

    @Column(name = "CAT_TIPO_ID")
    private int catTipoId;

    @Column(name = "STATUS")
    private Boolean status;

    @Column(name = "RFC", length = 20)
    private String rfc;

    @Column(name = "UC", length = 50)
    private String uc;

    @Column(name = "UM", length = 50)
    private String um;

    @Column(name = "FM")
    private LocalDate fm;

    @Column(name = "FC")
    private LocalDate fc;

    @Column(name = "ESTADO_CIVIL_ID")
    private int estadoCivilId;

    @Column(name = "TELEFONO", length = 50)
    private String telefono;

    @Column(name = "GENERO", length = 50)
    private String genero;
}