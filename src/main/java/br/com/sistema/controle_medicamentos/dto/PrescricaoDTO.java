package br.com.sistema.controle_medicamentos.dto;

import java.time.LocalDateTime;

// DTO para Criar/Atualizar Prescrição
public class PrescricaoDTO {

    // private Long medicamentoId; // REMOVIDO
    private Long itemInventarioId; // MODIFICADO
    private int quantidadePorDose; // NOVO

    private String dosagemPrescrita; // Ex: "1 comprimido"
    private LocalDateTime dataHoraInicio;
    private int intervaloHoras;
    private int duracaoDias;
    private String instrucoes;

    // --- Getters e Setters ---

    public Long getItemInventarioId() {
        return itemInventarioId;
    }

    public void setItemInventarioId(Long itemInventarioId) {
        this.itemInventarioId = itemInventarioId;
    }

    public int getQuantidadePorDose() {
        return quantidadePorDose;
    }

    public void setQuantidadePorDose(int quantidadePorDose) {
        this.quantidadePorDose = quantidadePorDose;
    }

    public String getDosagemPrescrita() {
        return dosagemPrescrita;
    }

    public void setDosagemPrescrita(String dosagemPrescrita) {
        this.dosagemPrescrita = dosagemPrescrita;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public int getIntervaloHoras() {
        return intervaloHoras;
    }

    public void setIntervaloHoras(int intervaloHoras) {
        this.intervaloHoras = intervaloHoras;
    }

    public int getDuracaoDias() {
        return duracaoDias;
    }

    public void setDuracaoDias(int duracaoDias) {
        this.duracaoDias = duracaoDias;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
    }
}

