package com.unipar.H_C_backend.repository;

import com.unipar.H_C_backend.domain.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
}
