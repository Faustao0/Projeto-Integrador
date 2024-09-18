package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.Consulta;
import com.unipar.H_C_backend.domain.Medico;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.ConsultaRepository;
import com.unipar.H_C_backend.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // Se houver médicos vinculados, garantir que não há duplicatas
        if (consulta.getMedicos() != null && !consulta.getMedicos().isEmpty()) {
            List<Medico> medicosGerenciados = buscarMedicos(consulta.getMedicos());
            consulta.setMedicos(removeMedicosDuplicados(medicosGerenciados, consulta.getId()));
        }
        return consultaRepository.save(consulta);
    }

    public Consulta update(Long id, Consulta consultaDetails) throws BusinessException {
        Consulta consulta = findById(id);

        // Atualize os atributos da consulta existente
        consulta.setData(consultaDetails.getData());
        consulta.setHora(consultaDetails.getHora());
        consulta.setLocal(consultaDetails.getLocal());
        consulta.setValor(consultaDetails.getValor());

        // Mantenha os médicos existentes se nenhum novo for fornecido
        if (consultaDetails.getMedicos() != null && !consultaDetails.getMedicos().isEmpty()) {
            List<Medico> medicosGerenciados = buscarMedicos(consultaDetails.getMedicos());
            consulta.setMedicos(medicosGerenciados);
        }
        // Não faça nada se a lista de médicos for nula ou vazia, mantendo os médicos existentes

        return consultaRepository.save(consulta);
    }


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

    // Função para remover médicos duplicados
    private List<Medico> removeMedicosDuplicados(List<Medico> medicos, Long consultaId) throws BusinessException {
        // Obtenha a consulta existente para evitar vinculações duplicadas de médicos
        Consulta consultaExistente = consultaRepository.findById(consultaId).orElse(null);
        if (consultaExistente != null) {
            List<Medico> medicosExistentes = consultaExistente.getMedicos();
            // Remover os médicos que já estão vinculados à consulta
            medicos.removeIf(medico -> medicosExistentes.stream()
                    .anyMatch(medicoExistente -> medicoExistente.getId().equals(medico.getId())));
        }
        return medicos;
    }
}