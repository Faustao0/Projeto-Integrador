package com.unipar.H_C_backend.domain;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "exames")
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do exame é obrigatório")
    @Size(max = 100, message = "O nome do exame não pode ter mais de 100 caracteres")
    private String nome;

    @NotBlank(message = "O tipo de exame é obrigatório")
    @Size(max = 50, message = "O tipo de exame não pode ter mais de 50 caracteres")
    private String tipo;

    @NotBlank(message = "A data do exame é obrigatória")
    private LocalDate data;

    // Construtores, Getters e Setters
    public Exame() {}

    public Exame(String nome, String tipo, LocalDate data) {
        this.nome = nome;
        this.tipo = tipo;
        this.data = data;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
