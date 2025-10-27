package br.com.sistema.controle_medicamentos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// Imports do Lombok removidos (Data, NoArgsConstructor, AllArgsConstructor)

import java.util.Objects;

@Entity 
@Table(name = "medicamentos") 
// Anotações @Data, @NoArgsConstructor, @AllArgsConstructor removidas
public class Medicamento {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String nome;
    private String laboratorio;
    private String dosagem;
    private int quantidadeEstoque;
    private String viaAdministracao; // Ex: Oral, Injetável

    // --- CONSTRUTORES MANUAIS ---

    public Medicamento() {
    }

    public Medicamento(Long id, String nome, String laboratorio, String dosagem, int quantidadeEstoque, String viaAdministracao) {
        this.id = id;
        this.nome = nome;
        this.laboratorio = laboratorio;
        this.dosagem = dosagem;
        this.quantidadeEstoque = quantidadeEstoque;
        this.viaAdministracao = viaAdministracao;
    }

    // --- GETTERS E SETTERS MANUAIS ---
    // (Isso corrigirá os erros no PrescricaoResponseDTO)

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

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getViaAdministracao() {
        return viaAdministracao;
    }

    public void setViaAdministracao(String viaAdministracao) {
        this.viaAdministracao = viaAdministracao;
    }

    // --- EQUALS E HASHCODE MANUAIS ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicamento that = (Medicamento) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }
}

