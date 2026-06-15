package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelRetiro;
import com.mutualidad.modulo_gerencia.Models.ModelRetiroCajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetiroCajeroRepository extends JpaRepository<ModelRetiroCajero, Integer> {

    ModelRetiroCajero findByRetiro(ModelRetiro retiro);
}
