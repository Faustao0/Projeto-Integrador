package com.unipar.H_C_backend.service;



import com.unipar.H_C_backend.domain.Exame;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.ExameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExameService {

    @Autowired
    private ExameRepository exameRepository;

    public List<Exame> findAll() {
        return exameRepository.findAll();
    }

    public Exame findById(Long id) throws BusinessException {
        return exameRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Exame não encontrado com o ID: " + id));
    }

    public Exame save(Exame exame) {
        return exameRepository.save(exame);
    }

    public Exame update(Long id, Exame exameDetails) throws BusinessException {
        Exame exame = findById(id);
        exame.setNome(exameDetails.getNome());
        exame.setTipo(exameDetails.getTipo());
        exame.setData(exameDetails.getData());
        return exameRepository.save(exame);
    }

    public void delete(Long id) throws BusinessException {
        Exame exame = findById(id);
        exameRepository.delete(exame);
    }

    public Exame findByName(String nome) throws BusinessException {
        return exameRepository.findByNome(nome)
                .orElseThrow(() -> new BusinessException("Exame não encontrado com o nome: " + nome));
    }
}
