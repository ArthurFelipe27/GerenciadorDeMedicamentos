package br.com.sistema.controle_medicamentos.dto;

import br.com.sistema.controle_medicamentos.model.Prescricao;
import java.time.LocalDateTime;

public class PrescricaoResponseDTO {
    
    private Long id;
    private LocalDateTime dataHoraInicio;
    private int intervaloHoras;
    private int duracaoDias;
    private String instrucoes;
    private String dosagemPrescrita;
    private int quantidadePorDose; 
    private Long itemInventarioId; 
    private MedicamentoSimplesDTO medicamento; 

    public PrescricaoResponseDTO(Prescricao p) {
        this.id = p.getId();
        this.dataHoraInicio = p.getDataHoraInicio();
        this.intervaloHoras = p.getIntervaloHoras();
        this.duracaoDias = p.getDuracaoDias();
        this.instrucoes = p.getInstrucoes();
        this.dosagemPrescrita = p.getDosagemPrescrita();
        this.quantidadePorDose = p.getQuantidadePorDose();
        this.itemInventarioId = p.getItemInventario().getId();
        // Esta linha está CORRETA
        this.medicamento = new MedicamentoSimplesDTO(p.getItemInventario().getMedicamento());
    }

    // --- Getters (Manuais) ---
    public Long getId() { return id; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public int getIntervaloHoras() { return intervaloHoras; }
    public int getDuracaoDias() { return duracaoDias; }
    public String getInstrucoes() { return instrucoes; }
    public String getDosagemPrescrita() { return dosagemPrescrita; }
    public int getQuantidadePorDose() { return quantidadePorDose; }
    public Long getItemInventarioId() { return itemInventarioId; }
    public MedicamentoSimplesDTO getMedicamento() { return medicamento; }
}

