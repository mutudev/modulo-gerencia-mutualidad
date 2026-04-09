package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<ModelEstado, Integer> {
    ModelEstado findByEstado(String estado);
}
