package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.Medicamento;
import com.unipar.H_C_backend.domain.Paciente;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<Medicamento> findAll() {
        return medicamentoRepository.findAll();
    }

    public Medicamento findById(Long id) throws BusinessException {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Medicamento não encontrado!"));
    }

    public Medicamento save(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    public Medicamento update(Long id, Medicamento medicamentoDetails) throws BusinessException {
        Medicamento medicamento = findById(id);

        medicamento.setNome(medicamentoDetails.getNome());
        medicamento.setDosagem(medicamentoDetails.getDosagem());
        medicamento.setFrequencia(medicamentoDetails.getFrequencia());
        medicamento.setFabricante(medicamentoDetails.getFabricante());
        medicamento.setHorarioTomar(medicamentoDetails.getHorarioTomar());

        return medicamentoRepository.save(medicamento);
    }

    public void delete(Long id) throws BusinessException {
        Medicamento medicamento = findById(id);
        medicamentoRepository.delete(medicamento);
    }

    public Medicamento findByName(String nome) throws BusinessException {
        return medicamentoRepository.findByNome(nome)
                .orElseThrow(() -> new BusinessException("Medicamento não encontrado com o nome: " + nome));
    }

    public List<Medicamento> findMedicamentosByUsuarioId(Long usuarioId) throws BusinessException {
        List<Medicamento> medicamentos = medicamentoRepository.findMedicamentosByUsuarioId(usuarioId);
        if (medicamentos.isEmpty()) {
            throw new BusinessException("Nenhum medicamento encontrado para o usuário com ID: " + usuarioId);
        }
        return medicamentos;
    }
}
