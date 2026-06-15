package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HISTORIAL_ACUMULADO_CONDONACION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelHistorialAcumCondonacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CREDITO_ID", nullable = false)
    private Integer creditoId;

    @Column(name = "NUM_CUOTA", nullable = false)
    private Integer numCuota;

    @Column(name = "OPERACION_ID", nullable = false)
    private Integer operacionId;

    @Column(name = "INTERES_ACUM_CON", nullable = false, precision = 18, scale = 2)
    private BigDecimal interesAcumCon;

    @Column(name = "MORA_ACUM_CON", nullable = false, precision = 18, scale = 2)
    private BigDecimal moraAcumCon;

    @Column(name = "ESTADO", nullable = false)
    private Boolean estado;

    @Column(name = "FR", nullable = false)
    private LocalDateTime fr;
}
