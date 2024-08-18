package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.Paciente;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Paciente findById(Long id) throws BusinessException {
        return pacienteRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new BusinessException("Paciente não encontrado com ID: " + id));
    }

    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente update(Long id, Paciente pacienteDetails) throws BusinessException {
        Paciente paciente = pacienteRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new BusinessException("Paciente não encontrado!"));

        paciente.setNome(pacienteDetails.getNome());
        paciente.setTelefone(pacienteDetails.getTelefone());
        paciente.setEmail(pacienteDetails.getEmail());
        paciente.setCpf(pacienteDetails.getCpf());
        paciente.setIdade(pacienteDetails.getIdade());
        paciente.setHistoricoMedico(pacienteDetails.getHistoricoMedico());

        return pacienteRepository.save(paciente);
    }

    public void delete(Long id) throws BusinessException {
        Paciente paciente = pacienteRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new BusinessException("Paciente não encontrado!"));

        pacienteRepository.delete(paciente);
    }

    public Paciente findByName(String nome) throws BusinessException {
        return pacienteRepository.findByNome(nome)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado com nome: " + nome));
    }
}
