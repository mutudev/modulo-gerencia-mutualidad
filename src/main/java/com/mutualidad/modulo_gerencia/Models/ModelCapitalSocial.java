package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "CAPITAL_SOCIAL")

public class ModelCapitalSocial {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @Column(name = "NUM_SOCIO")
  private int numSocio;

  @Column(name = "EMPRESA_COD")
  private String empresa_cod;

  @Column(name = "MONTO_CUBIERTO")
  private double monto_cubierto;

  @Column(name = "FC")
  private Date fc;

  @Column(name = "FP")
  private Date fp;
}
