package br.com.sistema.controle_medicamentos.dto;

import java.time.LocalDate;

// DTO para CRIAR ou ATUALIZAR um item no inventário
public class ItemInventarioDTO {

    private Long medicamentoId; // ID do Medicamento (Catálogo)
    private int quantidadeAtual;
    private int limiteAlerta;
    private LocalDate dataValidade; // Formato esperado: "YYYY-MM-DD"

    // --- Getters e Setters ---
    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(int quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public int getLimiteAlerta() {
        return limiteAlerta;
    }

    public void setLimiteAlerta(int limiteAlerta) {
        this.limiteAlerta = limiteAlerta;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }
}
