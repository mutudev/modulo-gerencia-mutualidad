package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelCapitalSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapitalSocialRepository extends JpaRepository<ModelCapitalSocial, Integer> {

    List<ModelCapitalSocial> findByNumSocio(int numSocio);
}
