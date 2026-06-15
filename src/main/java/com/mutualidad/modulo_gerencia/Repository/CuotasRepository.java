package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelCuotas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;

public interface CuotasRepository extends JpaRepository<ModelCuotas, Integer> {


    @Query(value = "EXEC pa_CalcularPagoDeCuotas ?1, ?2, ?3, ?4, ?5", nativeQuery = true)
    List<Object[]> pa_CalcularPagoDeCuotas(
            Integer creditoId,
            Double tasaInteres,
            Double tasaMora,
            Double tasaIva,
            LocalDate fechaDesembolso
    );

    ModelCuotas findByNumCuotaAndCreditoId(int numCuota, int creditoId);

    ModelCuotas findFirstByCreditoIdAndIsCondonadoIsFalseOrderByNumCuotaAsc(Integer creditoId);

}
