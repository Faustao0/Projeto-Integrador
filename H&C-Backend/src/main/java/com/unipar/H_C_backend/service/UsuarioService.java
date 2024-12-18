package com.unipar.H_C_backend.service;

import com.unipar.H_C_backend.domain.*;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.ConsultaRepository;
import com.unipar.H_C_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ConsultaRepository consultaRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) throws BusinessException {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID: " + id));
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, Usuario usuarioDetails) throws BusinessException {
        Usuario usuario = findById(id);

        usuario.setNome(usuarioDetails.getNome());
        usuario.setTelefone(usuarioDetails.getTelefone());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setCpf(usuarioDetails.getCpf());
        usuario.setSenha(usuarioDetails.getSenha());
        usuario.setEnderecos(usuarioDetails.getEnderecos());
        usuario.setPacientes(usuarioDetails.getPacientes());

        if (usuarioDetails.getConsultas() != null) {
            List<Consulta> consultas = new ArrayList<>();

            for (Consulta consultaDetails : usuarioDetails.getConsultas()) {
                if (consultaDetails.getId() == null) {
                    throw new BusinessException("O ID da consulta não pode ser nulo.");
                }

                Consulta consulta = consultaRepository.findById(consultaDetails.getId())
                        .orElseThrow(() -> new BusinessException("Consulta não encontrada com o ID: " + consultaDetails.getId()));

                consulta.setUsuario(usuario);
                consultas.add(consulta);
            }

            usuario.setConsultas(consultas);
        }

        return usuarioRepository.save(usuario);
    }

    public void delete(Long id) throws BusinessException {
        Usuario usuario = findById(id);
        usuarioRepository.delete(usuario);
    }

    public Usuario findByName(String nome) throws BusinessException {
        return usuarioRepository.findByNome(nome)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o nome: " + nome));
    }

    public Usuario authenticate(String email, String senha) throws BusinessException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o email: " + email));

        if (!usuario.getSenha().equals(senha)) {
            throw new BusinessException("Senha incorreta!");
        }
        return usuario;
    }

    public Usuario desvincularConsulta(Long usuarioId, Long consultaId) throws BusinessException {
        Usuario usuario = findById(usuarioId);

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada com o ID: " + consultaId));

        if (!usuario.getConsultas().contains(consulta)) {
            throw new BusinessException("Consulta não vinculada a este usuário.");
        }

        usuario.getConsultas().remove(consulta);

        consulta.setUsuario(null);

        consultaRepository.save(consulta);
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        Optional<Usuario> user = usuarioRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("Usuário com e-mail " + email + " não encontrado.");
        }
    }
}

