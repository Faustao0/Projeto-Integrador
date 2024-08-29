package com.unipar.H_C_backend.service;



import com.unipar.H_C_backend.domain.Usuario;
import com.unipar.H_C_backend.exceptions.BusinessException;
import com.unipar.H_C_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
}

