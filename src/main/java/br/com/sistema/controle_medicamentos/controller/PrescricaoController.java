package br.com.sistema.controle_medicamentos.controller;

import br.com.sistema.controle_medicamentos.dto.PrescricaoDTO;
import br.com.sistema.controle_medicamentos.dto.PrescricaoResponseDTO;
import br.com.sistema.controle_medicamentos.model.Medicamento;
import br.com.sistema.controle_medicamentos.model.Prescricao;
import br.com.sistema.controle_medicamentos.model.Usuario;
import br.com.sistema.controle_medicamentos.repository.MedicamentoRepository;
import br.com.sistema.controle_medicamentos.repository.PrescricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prescricoes")
public class PrescricaoController {

    @Autowired
    private PrescricaoRepository prescricaoRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    // Método helper para pegar o usuário logado
    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        return (Usuario) authentication.getPrincipal();
    }

    // LISTAR todas as prescrições DO USUÁRIO LOGADO
    @GetMapping
    public List<PrescricaoResponseDTO> listarPrescricoesDoUsuario() {
        Usuario usuario = getUsuarioLogado();
        List<Prescricao> prescricoes = prescricaoRepository.findByUsuarioId(usuario.getId());
        
        // Converte a lista de Entidades para a lista de DTOs
        return prescricoes.stream()
                .map(PrescricaoResponseDTO::new) // Usa o construtor do DTO
                .collect(Collectors.toList());
    }

    // LISTAR prescrições ATIVAS do usuário
    @GetMapping("/ativas")
    public List<PrescricaoResponseDTO> listarPrescricoesAtivas() {
        Usuario usuario = getUsuarioLogado();
        List<Prescricao> prescricoesAtivas = prescricaoRepository.findByUsuarioIdAndAtiva(usuario.getId(), true);
        
        // Converte para DTO
        return prescricoesAtivas.stream()
                .map(PrescricaoResponseDTO::new)
                .collect(Collectors.toList());
    }


    // ADICIONAR uma nova prescrição para o usuário logado
    @PostMapping
    public ResponseEntity<Prescricao> adicionarPrescricao(@RequestBody PrescricaoDTO dto) {
        Usuario usuario = getUsuarioLogado();
        
        // O DTO terá apenas o ID do medicamento, precisamos buscá-lo
        Medicamento medicamento = medicamentoRepository.findById(dto.medicamentoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento não encontrado"));

        Prescricao novaPrescricao = new Prescricao();
        novaPrescricao.setUsuario(usuario);
        novaPrescricao.setMedicamento(medicamento);
        novaPrescricao.setDosagemPrescrita(dto.dosagemPrescrita());
        novaPrescricao.setDataHoraInicio(dto.dataHoraInicio());
        novaPrescricao.setIntervaloHoras(dto.intervaloHoras());
        novaPrescricao.setDuracaoDias(dto.duracaoDias());
        novaPrescricao.setInstrucoes(dto.instrucoes());
        novaPrescricao.setAtiva(true); // Começa como ativa

        Prescricao salva = prescricaoRepository.save(novaPrescricao);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    // DELETAR uma prescrição (apenas se pertencer ao usuário)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPrescricao(@PathVariable Long id) {
        Usuario usuario = getUsuarioLogado();
        
        return prescricaoRepository.findById(id)
                .map(prescricao -> {
                    if (!prescricao.getUsuario().getId().equals(usuario.getId())) {
                        // Usuário tentando deletar prescrição de outro
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
                    }
                    prescricaoRepository.delete(prescricao);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

