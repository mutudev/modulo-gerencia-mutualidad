package com.mutualidad.modulo_gerencia.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumenCreditosDTO {
    private Integer numCreditos;
    private Double saldoTotal;
}
