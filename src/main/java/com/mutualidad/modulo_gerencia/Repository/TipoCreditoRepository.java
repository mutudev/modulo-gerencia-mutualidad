package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelTipoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCreditoRepository extends JpaRepository<ModelTipoCredito, Long> {

    ModelTipoCredito findByNombre(String nombre);
}
