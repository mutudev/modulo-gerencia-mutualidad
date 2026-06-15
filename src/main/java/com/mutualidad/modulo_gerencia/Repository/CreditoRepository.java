package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditoRepository extends JpaRepository<ModelCredito, Integer> {

    List<ModelCredito> findAllBySocio(int numSocio);

    @Query(value = "SELECT TOP 1 SALDO_CREDITO " +
            "FROM CUOTAS_EJEMPLO " +
            "WHERE FECHA_P_REALIZADA IS NOT NULL " +
            "AND CREDITO_ID = :creditoId " +
            "ORDER BY NUM_CUOTA DESC", nativeQuery = true)
    String traerCuotasParaSaldo(@Param("creditoId") int creditoId);

    List<ModelCredito> findAllBySocioAndEmpresaAndStatus(String socio, String empresa, int status);






}
