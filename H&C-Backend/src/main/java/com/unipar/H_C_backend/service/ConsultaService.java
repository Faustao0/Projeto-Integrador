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
        consulta.setDataHora(consultaDetails.getDataHora());
        consulta.setMedico(consultaDetails.getMedico());
        consulta.setLocal(consultaDetails.getLocal());
        return consultaRepository.save(consulta);
    }

    public void delete(Long id) throws BusinessException {
        Consulta consulta = findById(id);
        consultaRepository.delete(consulta);
    }

    public Consulta findByMedico(String medico) throws BusinessException {
        return consultaRepository.findByMedico(medico)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada com o médico: " + medico));
    }
}

