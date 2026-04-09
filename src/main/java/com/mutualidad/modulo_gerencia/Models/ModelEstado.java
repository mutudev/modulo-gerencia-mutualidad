package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "CAT_ESTADOS")
@Data
public class ModelEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "FR")
    private LocalDateTime fr;

}