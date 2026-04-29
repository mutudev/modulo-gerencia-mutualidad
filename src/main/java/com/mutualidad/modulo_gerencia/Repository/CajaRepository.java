package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CajaRepository extends JpaRepository<ModelCaja, Integer> {



    List<ModelCaja> findByEstadoAndAjusteAndFechaRegistro(boolean estado, int ajuste, LocalDate fr);

    List<ModelCaja> findByEstado(boolean estado);

    @Procedure(name = "Caja.pa_ProcesarCierre")
    String paProcesarCierre(
         String Resultado
    );

}
