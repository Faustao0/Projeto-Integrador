package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.Medicamento;
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
        medicamento.setValidade(medicamentoDetails.getValidade());
        medicamento.setFabricante(medicamentoDetails.getFabricante());

        return medicamentoRepository.save(medicamento);
    }

    public void delete(Long id) throws BusinessException {
        Medicamento medicamento = findById(id);
        medicamentoRepository.delete(medicamento);
    }
}
