package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CAT_PARENTESCO")
public class ModelParentesco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "PARENTESCO", nullable = false)
    private String parentesco;

    @Column(name = "FR", nullable = false)
    private LocalDateTime fr;


    @Override
    public String toString() {
        return parentesco;
    }

}
