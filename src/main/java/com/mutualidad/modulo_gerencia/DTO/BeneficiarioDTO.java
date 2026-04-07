package com.mutualidad.modulo_gerencia.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiarioDTO {
    private Integer id;
    private String beneficiario;
    private Integer socio;
    private String parentesco;
    private Boolean titular;
    private Integer porcentaje;
}
