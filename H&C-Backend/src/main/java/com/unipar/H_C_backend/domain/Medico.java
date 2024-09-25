package com.unipar.H_C_backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Medico extends Pessoa {

    @NotBlank(message = "O CRM é obrigatório.")
    private String crm;

    @NotBlank(message = "A especialidade é obrigatória.")
    private String especialidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id")
    @JsonBackReference
    private Consulta consulta;

    public Medico(Long id, String nome, String telefone, String email, String cpf, String crm, String especialidade, Consulta consulta) {
        super(id, nome, telefone, email, cpf);
        this.crm = crm;
        this.especialidade = especialidade;
        this.consulta = consulta;
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

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
}