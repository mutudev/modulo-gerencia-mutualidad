package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelCapitalSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapitalSocialRepository extends JpaRepository<ModelCapitalSocial, Integer> {

    List<ModelCapitalSocial> findByNumSocio(int numSocio)
            ;
    ModelCapitalSocial findByNumSocioAndEmpresaCod(
            int numSocio,
            String empresa_cod
    );

    @Query(value = "SELECT COALESCE(SUM(MONTO_CUBIERTO), 0) FROM CAPITAL_SOCIAL WHERE NUM_SOCIO = :numSocio", nativeQuery = true)
    Double sumarCapitalSocial(@Param("numSocio") int numSocio);
}
