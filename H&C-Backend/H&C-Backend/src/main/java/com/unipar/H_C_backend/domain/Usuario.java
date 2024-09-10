package com.unipar.H_C_backend.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Usuario extends Pessoa {

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    @JsonManagedReference // Lado que "gerencia" a referência
    private List<Consulta> consultas;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    @JsonManagedReference
    private List<Endereco> enderecos;

//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "usuario_id")
//    @JsonManagedReference
//    private List<Paciente> pacientes;

    public Usuario() {
        super();
    }

    public Usuario(Long id, String nome, String telefone, String email, String cpf, String senha, List<Consulta> consultas, List<Endereco> enderecos) {
        super(id, nome, telefone, email, cpf);
        this.senha = senha;
        this.consultas = consultas;
        this.enderecos = enderecos;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
        for (Consulta consulta : consultas) {
            consulta.setUsuario(this);
        }
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

//    public List<Paciente> getPacientes() {
//        return pacientes;
//    }
//
//    public void setPacientes(List<Paciente> pacientes) {
//        this.pacientes = pacientes;
//        for (Paciente paciente : pacientes) {
//            paciente.setUsuario(this);
//        }
//    }
}