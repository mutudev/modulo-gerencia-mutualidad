package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelEstadoCivil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoCivilRepository extends JpaRepository<ModelEstadoCivil, Integer> {
    ModelEstadoCivil findByEstadoCivil(String estadoCivil);
}
