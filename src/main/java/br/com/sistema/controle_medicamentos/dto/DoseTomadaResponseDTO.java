package br.com.sistema.controle_medicamentos.dto;

import br.com.sistema.controle_medicamentos.model.DoseTomada;
import java.time.LocalDateTime;

public class DoseTomadaResponseDTO {
    
    private Long id;
    private LocalDateTime dataHoraTomada;
    private String nomeMedicamento;
    private String dosagemPrescrita;
    private String status; 

    public DoseTomadaResponseDTO(DoseTomada dose) {
        this.id = dose.getId();
        this.dataHoraTomada = dose.getDataHoraTomada();
        this.status = dose.getStatus(); 
        
        // CORREÇÃO: O caminho agora é Prescricao -> ItemInventario -> Medicamento
        this.nomeMedicamento = dose.getPrescricao().getItemInventario().getMedicamento().getNome();
        this.dosagemPrescrita = dose.getPrescricao().getDosagemPrescrita();
    }

    // --- Getters (Manuais) ---
    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHoraTomada() {
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

