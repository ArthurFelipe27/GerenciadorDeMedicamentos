package br.com.sistema.controle_medicamentos.dto;

import java.time.LocalDateTime;

// DTO (Data Transfer Object) para criar uma nova prescrição
// Usamos isso para não ter que enviar o objeto "Usuario" inteiro no JSON,
// e para receber apenas o ID do medicamento.

public record PrescricaoDTO(
        Long medicamentoId, // ID do medicamento (do catálogo)
        String dosagemPrescrita,
        LocalDateTime dataHoraInicio,
        int intervaloHoras,
        int duracaoDias,
        String instrucoes
) {
}
