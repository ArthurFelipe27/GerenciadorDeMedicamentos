package br.com.sistema.controle_medicamentos.dto;

import br.com.sistema.controle_medicamentos.model.Medicamento;

// DTO usado para retornar apenas informações básicas do medicamento
// dentro de PrescricaoResponseDTO.
public record MedicamentoSimplesDTO(
        Long id,
        String nome,
        String dosagem
) {
    // *** CORREÇÃO: Adicionado um construtor ***
    // Os DTOs (PrescricaoResponseDTO e ItemInventarioResponseDTO)
    // estavam tentando chamar `new MedicamentoSimplesDTO(medicamento)`.
    // Esta correção permite que o `record` seja construído a partir da entidade.
    public MedicamentoSimplesDTO(Medicamento medicamento) {
        this(
            medicamento.getId(), 
            medicamento.getNome(), 
            medicamento.getDosagem()
        );
    }
}
