package com.unipar.H_C_backend.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class Paciente extends Pessoa {

    @NotNull(message = "A idade é obrigatória.")
    private Integer idade;

    @NotNull(message = "O histórico médico é obrigatório.")
    private String historicoMedico;

    public Paciente() {
    }

    public Paciente(Long id, String nome, String telefone, String email, String cpf, Integer idade, String historicoMedico) {
        super(id, nome, telefone, email, cpf);
        this.idade = idade;
        this.historicoMedico = historicoMedico;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getHistoricoMedico() {
        return historicoMedico;
    }

    public void setHistoricoMedico(String historicoMedico) {
        this.historicoMedico = historicoMedico;
    }
}