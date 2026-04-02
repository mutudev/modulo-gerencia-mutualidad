package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "RETIRO")
@Data
public class ModelRetiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "SOCIO", nullable = false)
    private Integer socio;

    @Column(name = "SALDO_ANT", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoAnt;

    @Column(name = "SALDO_NUE", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoNue;

    @Column(name = "MONTO_RETIRO", precision = 10, scale = 2)
    private BigDecimal montoRetiro;

    @Column(name = "ESTADO", nullable = false)
    private Boolean estado = true;

    @Column(name = "USUARIO_ID", nullable = false)
    private Integer usuarioId;

    @Column(name = "EMPRESA", length = 4)
    private String empresa;

    @Column(name = "FR", nullable = false)
    private LocalDate fr;

}
