package br.com.sistema.controle_medicamentos.dto;

import java.time.Instant; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***

public class RegistrarDoseDTO {
    
    private Long prescricaoId;
    private Instant dataHoraTomada; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***
    private String status; 

    // --- Getters e Setters (Sem Lombok) ---

    public Long getPrescricaoId() {
        return prescricaoId;
    }

    public void setPrescricaoId(Long prescricaoId) {
        this.prescricaoId = prescricaoId;
    }

    public Instant getDataHoraTomada() { // *** CORREÇÃO: Instant ***
        return dataHoraTomada;
    }

    public void setDataHoraTomada(Instant dataHoraTomada) { // *** CORREÇÃO: Instant ***
        this.dataHoraTomada = dataHoraTomada;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
