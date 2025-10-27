package br.com.sistema.controle_medicamentos.dto;

import java.time.LocalDateTime;

public class RegistrarDoseDTO {
    
    private Long prescricaoId;
    private LocalDateTime dataHoraTomada;
    private String status; // NOVO: "TOMADA" ou "PULADA"

    // --- Getters e Setters (Sem Lombok) ---

    public Long getPrescricaoId() {
        return prescricaoId;
    }

    public void setPrescricaoId(Long prescricaoId) {
        this.prescricaoId = prescricaoId;
    }

    public LocalDateTime getDataHoraTomada() {
        return dataHoraTomada;
    }

    public void setDataHoraTomada(LocalDateTime dataHoraTomada) {
        this.dataHoraTomada = dataHoraTomada;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

