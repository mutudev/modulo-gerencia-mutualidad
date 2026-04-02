package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "MODULO")
public class ModelModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "MODULO")
    private String modulo;

    @Column(name = "ESTADO")
    private boolean estado;

    @Column(name = "FC")
    private LocalDate fc;

    @Column(name = "FM")
    private LocalDate fm;

    @Column(name = "UM")
    private String um;
}