package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "HISTORIAL_CREDITO_CONDONACION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelHistorialCreditoCondonacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CREDITO_ID", nullable = false)
    private Integer creditoId;

    @Column(name = "USUARIO_ID", nullable = false)
    private Integer usuarioId;

    @Column(name = "NUM_CUOTA", nullable = false)
    private Integer numCuota;

    @Column(name = "FECHA_C", nullable = false)
    private LocalDate fechaC;

    @Column(name = "FECHA_V", nullable = false)
    private LocalDate fechaV;

    @Column(name = "CAPITAL_CONDONADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal capitalCondonado;

    @Column(name = "INTERES_CONDONADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal interesCondonado;

    @Column(name = "MORA_CONDONADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal moraCondonado;

    @Column(name = "TOTAL_CONDONADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCondonado;

    @Column(name = "SALDO_CREDITO", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoCredito;

    @Column(name = "OPERACION_ID", nullable = false)
    private Integer operacionId;

    @Column(name = "ESTADO", nullable = false)
    private Boolean estado;

    @Column(name = "FECHA_P_ANTERIOR")
    private LocalDate fechaPAnterior;

    @Column(name = "FECHA_P_FINALIZADO")
    private LocalDate fechaPFinalizado;

    @Column(name = "FR", nullable = false)
    private LocalDateTime fechaRegistro;

}
