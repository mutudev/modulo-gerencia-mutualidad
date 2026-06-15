package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelFechasExcluyentes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FechasExcluyentesRepository extends JpaRepository<ModelFechasExcluyentes, Long> {

    List<ModelFechasExcluyentes> findByEstado(boolean estado);

    @Query("SELECT f FROM ModelFechasExcluyentes f WHERE f.estado = :estado AND " +
            "(:inicio <= f.fechaFinal AND :fin >= f.fechaInicial) AND f.id <> :idExcluir")
    ModelFechasExcluyentes encontrarSolapamiento(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin,
            @Param("estado") boolean estado,
            @Param("idExcluir") Long idExcluir
    );

}
