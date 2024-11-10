package com.unipar.H_C_backend.repository;



import com.unipar.H_C_backend.domain.Consulta;
import com.unipar.H_C_backend.domain.Paciente;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("SELECT c FROM Consulta c WHERE c.usuario.id = :usuarioId AND (c.data > CURRENT_DATE OR (c.data = CURRENT_DATE AND c.hora >= CURRENT_TIME)) ORDER BY c.data ASC, c.hora ASC")
    Optional<Consulta> findConsultaMaisRecenteByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM Consulta c WHERE c.usuario.id = :usuarioId")
    List<Consulta> findConsultasByUsuarioId(@Param("usuarioId") Long usuarioId);

    Optional<Consulta> findByDataAndHoraAndUsuarioId(LocalDate data, LocalTime hora, Long usuarioId);

}