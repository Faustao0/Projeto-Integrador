package com.unipar.H_C_backend.domain;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data e hora da consulta são obrigatórias.")
    private LocalDateTime dataHora;

    @NotBlank(message = "O nome do médico é obrigatório.")
    @Column(length = 100)
    private String medico;

    @NotBlank(message = "O local da consulta é obrigatório.")
    @Column(length = 100)
    private String local;

    // Construtores, Getters e Setters
    public Consulta() {}

    public Consulta(LocalDateTime dataHora, String medico, String local) {
        this.dataHora = dataHora;
        this.medico = medico;
        this.local = local;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}

