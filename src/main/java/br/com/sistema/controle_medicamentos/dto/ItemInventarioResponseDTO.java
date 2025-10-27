package br.com.sistema.controle_medicamentos.dto;

import br.com.sistema.controle_medicamentos.model.ItemInventario;
import java.time.LocalDate;

public class ItemInventarioResponseDTO {

    private Long id;
    private int quantidadeAtual;
    private int limiteAlerta;
    private LocalDate dataValidade;
    private MedicamentoSimplesDTO medicamento; 
    private boolean alertaEstoque; 

    public ItemInventarioResponseDTO(ItemInventario item) {
        this.id = item.getId();
        this.quantidadeAtual = item.getQuantidadeAtual();
        this.limiteAlerta = item.getLimiteAlerta();
        this.dataValidade = item.getDataValidade();
        // Esta linha está CORRETA e funcionará agora que MedicamentoSimplesDTO e ItemInventario têm getters
        this.medicamento = new MedicamentoSimplesDTO(item.getMedicamento());
        this.alertaEstoque = item.getQuantidadeAtual() <= item.getLimiteAlerta();
    }

    // --- Getters (Manuais) ---
    public Long getId() {
        return id;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public int getLimiteAlerta() {
        return limiteAlerta;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public MedicamentoSimplesDTO getMedicamento() {
        return medicamento;
    }

    public boolean isAlertaEstoque() {
        return alertaEstoque;
    }
}

