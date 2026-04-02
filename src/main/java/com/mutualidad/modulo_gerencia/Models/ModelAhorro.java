package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "CUENTA_AHORRO")


@NamedStoredProcedureQuery(
        name = "Ahorro.pa_InsertarCuentadeAhorro",
        procedureName = "pa_InsertarCuentadeAhorro",
        parameters = {

                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NumeroSocio", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NumCuenta", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "RESULTADO", type = String.class)
        }
)

@NamedStoredProcedureQuery(
        name = "Ahorro.pa_InsertarCongelamiento",
        procedureName = "pa_InsertarCongelamiento",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "IdUsuario", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NumSocio", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "empresa", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "saldo_congelado", type = Double.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "Ahorro_alMomento", type = Double.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "opcion_con", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "Resultado", type = String.class)
        }
)

public class ModelAhorro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @Column(name = "SOCIO")
  private int socio;

  @Column(name = "NUM_CUENTA")
  private String num_cuenta;

  @Column(name = "SALDO")
  private double saldo;

  @Column(name = "SALDO_CONGELADO")
  private double saldo_congelado;

  @Column(name = "STATUS")
  private int status;

  @Column(name = "FC")
  private LocalDate fc;

}
