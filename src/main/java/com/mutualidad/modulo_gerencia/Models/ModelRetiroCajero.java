package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "RETIRO_CAJERO")
@Data
public class ModelRetiroCajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "RETIRO_ID", nullable = false)
    private ModelRetiro retiro;

    @Column(name = "ESTADO", nullable = false)
    private Boolean estado;

    @Column(name = "FR", nullable = false)
    private LocalDate fr;

}
