package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "TIPO_SOCIO", schema = "dbo")
public class ModelTipoSocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "DESCRIPCION", nullable = false, length = 20)
    private String descripcion;

    @Column(name = "FC", nullable = false)
    private LocalDate fc;
}
