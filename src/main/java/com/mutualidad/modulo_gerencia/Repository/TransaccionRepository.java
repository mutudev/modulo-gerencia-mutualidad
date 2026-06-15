package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<ModelTransaccion, Integer> {

    List<ModelTransaccion> findBySocioIdAndStatusAndOperacionIdIn(int socioId, boolean status, List<Integer> operacionIds);

    ModelTransaccion findBySocioIdAndStatusAndOperacionId(int socioId, boolean status, int operacionId );

    ModelTransaccion findBySocioIdAndStatusAndOperacionIdAndFechaRegistro(int socioId, boolean status, int operacionId, LocalDate fr);
}
