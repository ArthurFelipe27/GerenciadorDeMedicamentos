package br.com.sistema.controle_medicamentos.dto;

import br.com.sistema.controle_medicamentos.model.Prescricao;
import java.time.Instant; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***

public class PrescricaoResponseDTO {
    
    private Long id;
    private Instant dataHoraInicio; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***
    private int intervaloHoras;
    private int duracaoDias;
    private String instrucoes;
    private String dosagemPrescrita;
    private int quantidadePorDose; 
    private Long itemInventarioId; 
    private MedicamentoSimplesDTO medicamento; 

    public PrescricaoResponseDTO(Prescricao p) {
        this.id = p.getId();
        this.dataHoraInicio = p.getDataHoraInicio(); // *** CORREÇÃO: Instant ***
        this.intervaloHoras = p.getIntervaloHoras();
        this.duracaoDias = p.getDuracaoDias();
        this.instrucoes = p.getInstrucoes();
        this.dosagemPrescrita = p.getDosagemPrescrita();
        this.quantidadePorDose = p.getQuantidadePorDose();
        this.itemInventarioId = p.getItemInventario().getId();
        this.medicamento = new MedicamentoSimplesDTO(p.getItemInventario().getMedicamento());
    }

    // --- Getters (Manuais) ---
    public Long getId() { return id; }
    public Instant getDataHoraInicio() { return dataHoraInicio; } // *** CORREÇÃO: Instant ***
    public int getIntervaloHoras() { return intervaloHoras; }
    public int getDuracaoDias() { return duracaoDias; }
    public String getInstrucoes() { return instrucoes; }
    public String getDosagemPrescrita() { return dosagemPrescrita; }
    public int getQuantidadePorDose() { return quantidadePorDose; }
    public Long getItemInventarioId() { return itemInventarioId; }
    public MedicamentoSimplesDTO getMedicamento() { return medicamento; }
}
