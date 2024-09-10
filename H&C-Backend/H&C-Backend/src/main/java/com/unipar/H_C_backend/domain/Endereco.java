package com.unipar.H_C_backend.domain;



import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Rua não pode ser vazia")
    private String rua;

    @NotBlank(message = "Número não pode ser vazio")
    private String numero;

    @NotBlank(message = "Cidade não pode ser vazia")
    private String cidade;

    @NotBlank(message = "Estado não pode ser vazio")
    @Size(max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @NotBlank(message = "O nome do bairro  não pode ser vazio!")
    private String bairro;

    @NotBlank(message = "CEP não pode ser vazio")
    private String cep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "paciente_id")
//    @JsonBackReference
//    private Paciente paciente;

    public Endereco(Long id, String rua, String numero, String cidade, String estado, String bairro, String cep, Usuario usuario) {
        this.id = id;
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
        this.bairro = bairro;
        this.cep = cep;
        this.usuario = usuario;
    }

    public Endereco() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

//    public Paciente getPaciente() {
//        return paciente;
//    }
//
//    public void setPaciente(Paciente paciente) {
//        this.paciente = paciente;
//    }
}


