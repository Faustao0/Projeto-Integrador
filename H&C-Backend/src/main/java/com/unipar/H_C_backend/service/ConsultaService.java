package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.Consulta;
import com.unipar.H_C_backend.domain.Medico;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.ConsultaRepository;
import com.unipar.H_C_backend.repository.MedicoRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Consulta> findAll() {
        return consultaRepository.findAll();
    }

    public Consulta findById(Long id) throws BusinessException {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada com o ID: " + id));
    }

    public Consulta save(Consulta consulta) throws BusinessException {
        if (consulta.getMedicos() != null && !consulta.getMedicos().isEmpty()) {
            List<Medico> medicosGerenciados = buscarMedicos(consulta.getMedicos());
            consulta.setMedicos(medicosGerenciados);
        }

        return consultaRepository.save(consulta);
    }

    public Consulta update(Long id, Consulta consultaDetails) throws BusinessException {
        Consulta consulta = findById(id);

        consulta.setData(consultaDetails.getData());
        consulta.setHora(consultaDetails.getHora());
        consulta.setLocal(consultaDetails.getLocal());
        consulta.setValor(consultaDetails.getValor());

        if (consultaDetails.getMedicos() != null && !consultaDetails.getMedicos().isEmpty()) {
            List<Medico> medicosGerenciados = buscarMedicos(consultaDetails.getMedicos());
            consulta.setMedicos(medicosGerenciados);
        }

        return consultaRepository.save(consulta);
    }

    @Transactional
    public void delete(Long id) throws BusinessException {
        Consulta consulta = findById(id);
        consultaRepository.delete(consulta);
    }

    private List<Medico> buscarMedicos(List<Medico> medicos) throws BusinessException {
        List<Medico> medicosGerenciados = new ArrayList<>();
        for (Medico medico : medicos) {
            Optional<Medico> medicoGerenciado = medicoRepository.findById(medico.getId());
            if (medicoGerenciado.isPresent()) {
                medicosGerenciados.add(medicoGerenciado.get());
            } else {
                throw new BusinessException("Médico não encontrado com o ID: " + medico.getId());
            }
        }
        return medicosGerenciados;
    }

    public Consulta findConsultaMaisRecenteByUsuarioId(Long usuarioId) throws BusinessException {
        Optional<Consulta> consultaMaisRecente = consultaRepository.findConsultaMaisRecenteByUsuarioId(usuarioId);
        return consultaMaisRecente.orElseThrow(() -> new BusinessException("Nenhuma consulta futura encontrada para o usuário com ID: " + usuarioId));
    }

    public List<Consulta> findConsultasByUsuarioId(Long usuarioId) throws BusinessException {
        List<Consulta> consultas = consultaRepository.findConsultasByUsuarioId(usuarioId);
        if (consultas.isEmpty()) {
            throw new BusinessException("Nenhuma consulta encontrada para o usuário com ID: " + usuarioId);
        }
        return consultas;
    }

    public boolean horarioOcupado(LocalDate data, LocalTime hora, Long usuarioId) {
        return consultaRepository.findByDataAndHoraAndUsuarioId(data, hora, usuarioId).isPresent();
    }

    public Consulta agendarConsulta(Consulta consulta) throws Exception {
        if (horarioOcupado(consulta.getData(), consulta.getHora(), consulta.getUsuario().getId())) {
            throw new Exception("Horário já ocupado para este usuário.");
        }
        return consultaRepository.save(consulta);
    }
}