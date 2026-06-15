package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FECHAS_EXCLUYENTES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelFechasExcluyentes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "FECHA_INICIAL", nullable = false)
    private LocalDate fechaInicial;

    @Column(name = "FECHA_FINAL", nullable = false)
    private LocalDate fechaFinal;

    @Column(name = "DESCRIPCION", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "ESTADO", nullable = false)
    private Boolean estado;

    @Column(name = "FR", nullable = false)
    private LocalDateTime fr;

    @Column(name = "UR", nullable = false)
    private Integer ur;
}
