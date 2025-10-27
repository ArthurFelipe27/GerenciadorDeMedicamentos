package br.com.sistema.controle_medicamentos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicamentos") 
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String laboratorio;
    private String dosagem;
    private String viaAdministracao; 

    // --- Construtores (Sem Lombok) ---
    public Medicamento() {}
    
    public Medicamento(Long id, String nome, String laboratorio, String dosagem, String viaAdministracao) {
        this.id = id;
        this.nome = nome;
        this.laboratorio = laboratorio;
        this.dosagem = dosagem;
        this.viaAdministracao = viaAdministracao;
    }

    // --- Getters e Setters (Manuais) ---
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

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getViaAdministracao() {
        return viaAdministracao;
    }

    public void setViaAdministracao(String viaAdministracao) {
        this.viaAdministracao = viaAdministracao;
    }
}

