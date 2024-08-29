package com.unipar.H_C_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Medicamento {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do medicamento é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome do medicamento deve ter entre 2 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "A dosagem do medicamento é obrigatória.")
    private String dosagem;

    @NotBlank(message = "A frequência de administração do medicamento é obrigatória.")
    private String frequencia;

    @NotNull(message = "A data de validade do medicamento é obrigatória.")
    private String validade;

    @NotBlank(message = "O fabricante do medicamento é obrigatório.")
    private String fabricante;


    public Medicamento() {
    }

    public Medicamento(Long id, String nome, String dosagem, String frequencia, String validade, String fabricante) {
        this.id = id;
        this.nome = nome;
        this.dosagem = dosagem;
        this.frequencia = frequencia;
        this.validade = validade;
        this.fabricante = fabricante;
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

}
