package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.Medicamento;
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

    @Autowired
    private MedicamentoService medicamentoService;

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Paciente findById(Long id) throws BusinessException {
        return pacienteRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new BusinessException("Paciente não encontrado com ID: " + id));
    }

    public Paciente findByName(String nome) throws BusinessException {
        return pacienteRepository.findByNome(nome)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado com nome: " + nome));
    }

    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente update(Long id, Paciente pacienteDetails) throws BusinessException {
        Paciente paciente = findById(id);

        paciente.setNome(pacienteDetails.getNome());
        paciente.setTelefone(pacienteDetails.getTelefone());
        paciente.setEmail(pacienteDetails.getEmail());
        paciente.setCpf(pacienteDetails.getCpf());
        paciente.setIdade(pacienteDetails.getIdade());
        if (pacienteDetails.getMedicamentos() != null) {
            paciente.setMedicamentos(pacienteDetails.getMedicamentos());
        }
        paciente.setEnderecos(pacienteDetails.getEnderecos());
        paciente.setUsuario(pacienteDetails.getUsuario());

        return pacienteRepository.save(paciente);
    }

    public Paciente updateByName(String nome, Paciente pacienteDetails) throws BusinessException {
        Paciente paciente = findByName(nome);

        paciente.setNome(pacienteDetails.getNome());
        paciente.setTelefone(pacienteDetails.getTelefone());
        paciente.setEmail(pacienteDetails.getEmail());
        paciente.setCpf(pacienteDetails.getCpf());
        paciente.setIdade(pacienteDetails.getIdade());
        if (pacienteDetails.getMedicamentos() != null) {
            paciente.setMedicamentos(pacienteDetails.getMedicamentos());
        }
        paciente.setEnderecos(pacienteDetails.getEnderecos());
        paciente.setUsuario(pacienteDetails.getUsuario());

        return pacienteRepository.save(paciente);
    }

    public Paciente vincularMedicamento(String nome, Long medicamentoId) throws BusinessException {
        Paciente paciente = findByName(nome);
        Medicamento medicamento = medicamentoService.findById(medicamentoId);

        paciente.getMedicamentos().add(medicamento);

        return save(paciente);
    }

    public void delete(Long id) throws BusinessException {
        Paciente paciente = findById(id);
        pacienteRepository.delete(paciente);
    }

    public List<Paciente> findByUsuarioId(Long usuarioId) throws BusinessException {
        List<Paciente> pacientes = pacienteRepository.findByUsuarioId(usuarioId);
        if (pacientes.isEmpty()) {
            throw new BusinessException("Nenhum paciente encontrado para o usuário com ID: " + usuarioId);
        }
        return pacientes;
    }
}