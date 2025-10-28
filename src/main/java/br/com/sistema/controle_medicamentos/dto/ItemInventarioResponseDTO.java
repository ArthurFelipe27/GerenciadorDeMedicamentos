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
    private boolean vencido; // *** SUGESTÃO 2: Adicionado ***

    public ItemInventarioResponseDTO(ItemInventario item) {
        this.id = item.getId();
        this.quantidadeAtual = item.getQuantidadeAtual();
        this.limiteAlerta = item.getLimiteAlerta();
        this.dataValidade = item.getDataValidade();
        this.medicamento = new MedicamentoSimplesDTO(item.getMedicamento());
        
        // Lógica de alerta e validade
        this.alertaEstoque = item.getQuantidadeAtual() <= item.getLimiteAlerta();
        // *** SUGESTÃO 2: Lógica de validade adicionada ***
        this.vencido = (item.getDataValidade() != null && item.getDataValidade().isBefore(LocalDate.now()));
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

    // *** SUGESTÃO 2: Getter adicionado ***
    public boolean isVencido() {
        return vencido;
    }
}
