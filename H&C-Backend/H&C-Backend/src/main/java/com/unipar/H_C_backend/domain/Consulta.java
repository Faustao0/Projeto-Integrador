package com.unipar.H_C_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data da consulta é obrigatória.")
    private LocalDate data;

    @NotNull(message = "A hora da consulta é obrigatória.")
    private LocalTime hora;

    @NotBlank(message = "O local da consulta é obrigatório.")
    private String local;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "consulta_id")
    private List<Medico> medicos;


    public Consulta() {}

    public Consulta(Long id, LocalDate data, LocalTime hora, String local, List<Medico> medicos) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.local = local;
        this.medicos = medicos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }
}