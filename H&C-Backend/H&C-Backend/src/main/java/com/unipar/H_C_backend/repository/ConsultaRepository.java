package com.unipar.H_C_backend.repository;



import com.unipar.H_C_backend.domain.Consulta;
import com.unipar.H_C_backend.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}