package com.example.hc_frontend.domain;

import java.io.Serializable;
import java.util.List;

public class Medicamento implements Serializable {

    private Long id;
    private String nome;
    private String dosagem;
    private String frequencia;
    private String validade;
    private String fabricante;
    private String horarioTomar;
    private List<Paciente> pacientes;

    public Medicamento(String nome, String dosagem, String frequencia, String fabricante, String horarioTomar) {
        this.nome = nome;
        this.dosagem = dosagem;
        this.frequencia = frequencia;
        this.fabricante = fabricante;
        this.horarioTomar = horarioTomar;
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

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getHorarioTomar() {
        return horarioTomar;
    }

    public void setHorarioTomar(String horarioTomar) {
        this.horarioTomar = horarioTomar;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }
}
