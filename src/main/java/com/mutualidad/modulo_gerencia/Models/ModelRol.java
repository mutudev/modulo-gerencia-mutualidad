package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "ROL")
public class ModelRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ROL", nullable = false, length = 20)
    private String rol;

    @Column(name = "FC", nullable = false)
    private LocalDate fc;

    @Column(name = "FM", nullable = false)
    private LocalDate fm;

    @Column(name = "UM", nullable = false, length = 15)
    private String um;
}
