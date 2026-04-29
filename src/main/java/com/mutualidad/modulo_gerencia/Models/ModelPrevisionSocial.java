package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PREVISION_SOCIAL")
@Data
public class ModelPrevisionSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NUM_SOCIO", nullable = false)
    private Integer numSocio;

    @Column(name = "EMPRESA_COD", nullable = false, length = 4)
    private String empresaCod;

    @Column(name = "PREVISION", nullable = false, precision = 10, scale = 2)
    private BigDecimal prevision;

    @Column(name = "MONTO_ASIGNADO", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoAsignado;

    @Column(name = "FECHA_PAGO")
    private LocalDate fechaPago;

    @Column(name = "UR", nullable = false)
    private Integer ur;

}