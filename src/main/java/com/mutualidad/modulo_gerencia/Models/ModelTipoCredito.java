package com.mutualidad.modulo_gerencia.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TIPO_CREDITO", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelTipoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CODIGO_SISTEMA", nullable = false, length = 6)
    private String codigoSistema;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "FC", nullable = false)
    @ColumnDefault("getdate()")
    private LocalDate fc;

    @Column(name = "BONIF")
    private Boolean bonif;

    @Column(name = "MONTO_MAXIMO", precision = 18, scale = 2)
    private BigDecimal montoMaximo;

    @Column(name = "IVA")
    private Boolean iva;

    @Override
    public String toString() {
        return nombre;
    }

}