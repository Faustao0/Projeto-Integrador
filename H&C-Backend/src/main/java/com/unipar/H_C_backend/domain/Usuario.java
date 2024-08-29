package com.unipar.H_C_backend.domain;



import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Usuario extends Pessoa {

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    public Usuario() {
        super();
    }

    public Usuario(Long id, String nome, String telefone, String email, String cpf, String senha) {
        super(id, nome, telefone, email, cpf);
        this.senha = senha;
    }

    // Getters and Setters
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

