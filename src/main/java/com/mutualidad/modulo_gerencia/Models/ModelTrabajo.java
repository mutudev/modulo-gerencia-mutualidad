package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "CAT_TRABAJO")
@Data
public class ModelTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "TRABAJO")
    private String trabajo;

    @Column(name = "FR")
    private LocalDate fr;

}
