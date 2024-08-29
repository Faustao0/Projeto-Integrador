package com.unipar.H_C_backend.repository;



import com.unipar.H_C_backend.domain.Exame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExameRepository extends JpaRepository<Exame, Long> {
    Optional<Exame> findByNome(String nome);
}
