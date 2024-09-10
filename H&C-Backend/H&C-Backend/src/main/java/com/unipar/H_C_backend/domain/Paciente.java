package com.unipar.H_C_backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Paciente extends Pessoa {

    @NotNull(message = "A idade é obrigatória.")
    private Integer idade;

    @NotNull(message = "O histórico médico é obrigatório.")
    private String historicoMedico;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "paciente_id")
    @JsonManagedReference
    private List<Medicamento> medicamentos;

//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "usuario_id")
//    @JsonManagedReference
//    private List<Endereco> enderecos;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "usuario_id")
//    @JsonBackReference
//    private Usuario usuario;

    public Paciente() {
    }

    public Paciente(Long id, String nome, String telefone, String email, String cpf, Integer idade, String historicoMedico, List<Medicamento> medicamentos) {
        super(id, nome, telefone, email, cpf);
        this.idade = idade;
        this.historicoMedico = historicoMedico;
        this.medicamentos = medicamentos;
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

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
        for (Medicamento medicamento : medicamentos) {
            medicamento.setPaciente(this);
        }
    }

//    public List<Endereco> getEnderecos() {
//        return enderecos;
//    }
//
//    public void setEnderecos(List<Endereco> enderecos) {
//        this.enderecos = enderecos;
//    }

//    public Usuario getUsuario() {
//        return usuario;
//    }
//
//    public void setUsuario(Usuario usuario) {
//        this.usuario = usuario;
//    }
}
