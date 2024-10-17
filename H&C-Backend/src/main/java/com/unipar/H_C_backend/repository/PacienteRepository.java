package com.unipar.H_C_backend.repository;

import com.unipar.H_C_backend.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByNome(String nome);

    List<Paciente> findByUsuarioId(Long usuarioId);
}
