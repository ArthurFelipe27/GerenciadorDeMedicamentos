package br.com.sistema.controle_medicamentos.dto;

import br.com.sistema.controle_medicamentos.model.DoseTomada;
import java.time.Instant; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***

public class DoseTomadaResponseDTO {
    
    private Long id;
    private Instant dataHoraTomada; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***
    private String nomeMedicamento;
    private String dosagemPrescrita;
    private String status; 

    public DoseTomadaResponseDTO(DoseTomada dose) {
        this.id = dose.getId();
        this.dataHoraTomada = dose.getDataHoraTomada(); // *** CORREÇÃO: Instant ***
        this.status = dose.getStatus(); 
        
        this.nomeMedicamento = dose.getPrescricao().getItemInventario().getMedicamento().getNome();
        this.dosagemPrescrita = dose.getPrescricao().getDosagemPrescrita();
    }

    // --- Getters (Manuais) ---
    public Long getId() {
        return id;
    }

    public Instant getDataHoraTomada() { // *** CORREÇÃO: Instant ***
        return dataHoraTomada;
    }

    public String getNomeMedicamento() {
        return nomeMedicamento;
    }

    public String getDosagemPrescrita() {
        return dosagemPrescrita;
    }

    public String getStatus() {
        return status;
    }
}
