package com.unipar.H_C_backend.service;


import com.unipar.H_C_backend.domain.Endereco;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Endereco> findAll() {
        return enderecoRepository.findAll();
    }

    public Endereco findById(Long id) throws BusinessException {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado com o ID: " + id));
    }

    public Endereco save(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco update(Long id, Endereco enderecoDetails) throws BusinessException {
        Endereco endereco = findById(id);
        endereco.setRua(enderecoDetails.getRua());
        endereco.setNumero(enderecoDetails.getNumero());
        endereco.setCidade(enderecoDetails.getCidade());
        endereco.setEstado(enderecoDetails.getEstado());
        endereco.setCep(enderecoDetails.getCep());
        endereco.setBairro(enderecoDetails.getBairro());
        endereco.setPessoa(enderecoDetails.getPessoa());
        return enderecoRepository.save(endereco);
    }

    public void delete(Long id) throws BusinessException {
        Endereco endereco = findById(id);
        enderecoRepository.delete(endereco);
    }

    public Endereco findByCep(String cep) throws BusinessException {
        return enderecoRepository.findByCep(cep)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado com o CEP: " + cep));
    }
}

