package com.unipar.H_C_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Medico extends Pessoa {

    @NotBlank(message = "O CRM é obrigatório.")
    private String crm;

    @NotBlank(message = "A especialidade é obrigatória.")
    private String especialidade;

    public Medico(Long id, String nome, String telefone, String email, String cpf, String crm, String especialidade) {
        super(id, nome, telefone, email, cpf);
        this.crm = crm;
        this.especialidade = especialidade;
    }

    public Medico() {}

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
