package com.mutualidad.modulo_gerencia.Repository;

import com.mutualidad.modulo_gerencia.Models.ModelRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<ModelRol, Integer> {
}
