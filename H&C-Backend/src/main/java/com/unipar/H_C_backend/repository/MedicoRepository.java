package com.unipar.H_C_backend.repository;





import com.unipar.H_C_backend.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByCrm(String crm);
    Optional<Medico> findByEspecialidade(String especialidade);
}

