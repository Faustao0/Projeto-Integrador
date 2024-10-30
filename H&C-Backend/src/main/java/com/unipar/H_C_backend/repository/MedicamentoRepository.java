package com.unipar.H_C_backend.repository;

import com.unipar.H_C_backend.domain.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByNome(String nome);

    @Query("SELECT m FROM Medicamento m WHERE m.paciente.usuario.id = :usuarioId")
    List<Medicamento> findMedicamentosByUsuarioId(@Param("usuarioId") Long usuarioId);
}
