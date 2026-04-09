package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "CAT_MUNICIPIOS")
@Data
public class ModelMunicipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "MUNICIPIO")
    private String municipio;

    @Column(name = "ID_ESTADO")
    private Integer idEstado;

    @Column(name = "FR")
    private LocalDateTime fr;

}
