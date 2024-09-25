package com.unipar.H_C_backend.repository;



import com.unipar.H_C_backend.domain.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    Optional<Endereco> findByCep(String cep);
}

