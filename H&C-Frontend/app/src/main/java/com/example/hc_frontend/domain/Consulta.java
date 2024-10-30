package com.example.hc_frontend.domain;

import java.util.List;
import java.io.Serializable;

public class Consulta implements Serializable{

    private Long id;
    private String data;
    private String hora;
    private String local;
    private Double valor;
    private List<Medico> medicos;
    private Usuario usuario;

    public Consulta(Long id, String data, String hora, String local, Double valor, List<Medico> medicos, Usuario usuario) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.local = local;
        this.valor = valor;
        this.medicos = medicos;
        this.usuario = usuario;
    }

    public Consulta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
