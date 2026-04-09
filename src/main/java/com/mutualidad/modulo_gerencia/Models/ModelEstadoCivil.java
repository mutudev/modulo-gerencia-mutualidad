package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "CAT_ESTADO_CIVIL")
@Data
public class ModelEstadoCivil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ESTADO_CIVIL")
    private String estadoCivil;

    @Column(name = "FR")
    private LocalDate fr;

}