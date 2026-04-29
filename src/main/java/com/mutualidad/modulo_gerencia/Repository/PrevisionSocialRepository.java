package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelPrevisionSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrevisionSocialRepository extends JpaRepository<ModelPrevisionSocial, Integer> {

    ModelPrevisionSocial findByNumSocioAndEmpresaCod(int numSocio, String empresaCod);
}
