package com.unipar.H_C_backend.service;



import com.unipar.H_C_backend.domain.Medicamento;
import com.unipar.H_C_backend.domain.Medico;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> findAll() {
        return medicoRepository.findAll();
    }

    public Medico findById(Long id) throws BusinessException {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Medico não encontrado!"));
    }

    public Medico save(Medico medico) {
        return medicoRepository.save(medico);
    }

    public Medico update(Long id, Medico medicoDetails) throws BusinessException {
        Medico medico = findById(id);

        medico.setNome(medicoDetails.getNome());
        medico.setEspecialidade(medicoDetails.getEspecialidade());
        medico.setCrm(medicoDetails.getCrm());
        medico.setTelefone(medicoDetails.getTelefone());
        medico.setEmail(medicoDetails.getEmail());

        return medicoRepository.save(medico);
    }

    public void delete(Long id) throws BusinessException {
        Medico medico = findById(id);
        medicoRepository.delete(medico);
    }

    public Medico findByName(String Crm) throws BusinessException {
        return medicoRepository.findByCrm(Crm)
                .orElseThrow(() -> new BusinessException("Medico não encontrado com o nome: " + Crm));
    }
}
