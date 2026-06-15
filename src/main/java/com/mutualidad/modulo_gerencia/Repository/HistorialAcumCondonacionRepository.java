package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelHistorialAcumCondonacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialAcumCondonacionRepository extends JpaRepository<ModelHistorialAcumCondonacion, Long> {
}
