package com.example.hc_frontend.domain;

import java.util.List;
import java.io.Serializable;

public class Usuario implements Serializable{

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
    private String senha;
    private List<Consulta> consultas;

    public Usuario() {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cpf = cpf;
        this.senha = senha;
        this.consultas = consultas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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
    }
}