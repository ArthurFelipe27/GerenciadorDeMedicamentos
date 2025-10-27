package br.com.sistema.controle_medicamentos.dto;

// DTO usado para retornar apenas informações básicas do medicamento
// dentro de PrescricaoResponseDTO.
public record MedicamentoSimplesDTO(
        Long id,
        String nome,
        String dosagem
) {
}

