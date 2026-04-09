package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelPuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuestoRepository extends JpaRepository<ModelPuesto, Integer> {
}
