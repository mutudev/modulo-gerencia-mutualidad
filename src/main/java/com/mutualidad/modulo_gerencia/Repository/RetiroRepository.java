package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelRetiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetiroRepository extends JpaRepository<ModelRetiro, Integer> {

    List<ModelRetiro> findBySocioAndEstado(int socio, boolean estado);
}
