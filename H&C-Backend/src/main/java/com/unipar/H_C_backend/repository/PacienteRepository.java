package com.unipar.H_C_backend.repository;

import com.unipar.H_C_backend.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
}
