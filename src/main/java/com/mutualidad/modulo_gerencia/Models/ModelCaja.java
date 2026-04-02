package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "CAJA")
public class ModelCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "USUARIO_ID", nullable = false)
    private Integer usuarioId;

    @Column(name = "SAL_INICIAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal salInicial;

    @Column(name = "SAL_FINAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal salFinal;

    @Column(name = "FR", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "ESTADO", nullable = false)
    private Boolean estado;

    @Column(name = "TURNO", nullable = false, length = 50)
    private String turno;

    @Column(name = "EMPRESA", nullable = false, length = 4)
    private String empresa;

    @Column(name = "AJUSTE", nullable = false)
    private Integer ajuste;

    @Column(name = "CIERRE", nullable = false)
    private Integer cierre;

}