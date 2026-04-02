package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "EMPLEADO")
public class ModelEmpleado {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "NOMBRES")
    private String nombres;

    @Column(name = "APELLIDO_P")
    private String apellidoP;

    @Column(name = "APELLIDO_M")
    private String apellidoM;

    @Column(name = "F_NACIMIENTO")
    private LocalDate fecNacimiento;

    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "PUESTO_ID")
    private int puesto;

    @Column(name = "FC")
    private LocalDate fechaCreacion;


}
