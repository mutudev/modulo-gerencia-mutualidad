package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelHistorialCreditoCondonacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialCreditoCondonacionRepository extends JpaRepository<ModelHistorialCreditoCondonacion, Long> {

    List<ModelHistorialCreditoCondonacion> findByCreditoIdAndEstado(int creditoId, boolean estado);
}
