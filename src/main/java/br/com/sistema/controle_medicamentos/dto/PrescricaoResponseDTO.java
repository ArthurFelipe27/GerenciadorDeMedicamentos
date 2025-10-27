package br.com.sistema.controle_medicamentos.dto;

import br.com.sistema.controle_medicamentos.model.Prescricao;

import java.time.LocalDateTime;

public record PrescricaoResponseDTO(
        Long id,
        MedicamentoSimplesDTO medicamento, 
        String dosagemPrescrita,
        LocalDateTime dataHoraInicio,
        int intervaloHoras,
        int duracaoDias,
        String instrucoes,
        boolean ativa
) {
    // Método construtor auxiliar para facilitar a conversão
    // Agora que Prescricao tem getters manuais, isso vai funcionar.
    public PrescricaoResponseDTO(Prescricao p) {
        this(
                p.getId(),
                new MedicamentoSimplesDTO(
                        p.getMedicamento().getId(),
                        p.getMedicamento().getNome(),
                        p.getMedicamento().getDosagem()
                ),
                p.getDosagemPrescrita(),
                p.getDataHoraInicio(),
                p.getIntervaloHoras(),
                p.getDuracaoDias(),
                p.getInstrucoes(),
                p.isAtiva()
        );
    }
}

