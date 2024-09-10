package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.Consulta;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public List<Consulta> findAll() {
        return consultaRepository.findAll();
    }

    public Consulta findById(Long id) throws BusinessException {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada com o ID: " + id));
    }

    public Consulta save(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    public Consulta update(Long id, Consulta consultaDetails) throws BusinessException {
        Consulta consulta = findById(id);
        consulta.setData(consultaDetails.getData());
        consulta.setHora(consultaDetails.getHora());
        consulta.setLocal(consultaDetails.getLocal());
        consulta.setValor(consultaDetails.getValor());
        consulta.setMedicos(consultaDetails.getMedicos());
        return consultaRepository.save(consulta);
    }

    public void delete(Long id) throws BusinessException {
        Consulta consulta = findById(id);
        consultaRepository.delete(consulta);
    }
}