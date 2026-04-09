package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelTipoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoSocioRepository extends JpaRepository<ModelTipoSocio, Integer> {
}
