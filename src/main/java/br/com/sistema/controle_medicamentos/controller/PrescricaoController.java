package br.com.sistema.controle_medicamentos.controller;

import br.com.sistema.controle_medicamentos.dto.PrescricaoDTO;
import br.com.sistema.controle_medicamentos.dto.PrescricaoResponseDTO;
import br.com.sistema.controle_medicamentos.model.ItemInventario; 
import br.com.sistema.controle_medicamentos.model.Prescricao;
import br.com.sistema.controle_medicamentos.model.Usuario;
import br.com.sistema.controle_medicamentos.repository.ItemInventarioRepository; 
import br.com.sistema.controle_medicamentos.repository.PrescricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prescricoes")
public class PrescricaoController {

    @Autowired
    private PrescricaoRepository prescricaoRepository;

    @Autowired
    private ItemInventarioRepository inventarioRepository; 

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    // GET /api/prescricoes (Listar prescrições do usuário)
    @GetMapping
    public ResponseEntity<List<PrescricaoResponseDTO>> listarPrescricoesDoUsuario() {
        Usuario usuario = getUsuarioLogado();
        
        // CORREÇÃO: Chamando o método renomeado 'findByUsuarioId'
        List<Prescricao> prescricoes = prescricaoRepository.findByUsuarioId(usuario.getId()); 

        List<PrescricaoResponseDTO> dtos = prescricoes.stream()
                .map(PrescricaoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/prescricoes/ativas (Listar prescrições ativas)
    @GetMapping("/ativas")
    public ResponseEntity<List<PrescricaoResponseDTO>> listarPrescricoesAtivas() {
        Usuario usuario = getUsuarioLogado();
        LocalDateTime agora = LocalDateTime.now();
        List<Prescricao> prescricoes = prescricaoRepository.findPrescricoesAtivas(usuario.getId(), agora);

        List<PrescricaoResponseDTO> dtos = prescricoes.stream()
                .map(PrescricaoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST /api/prescricoes (Adicionar nova prescrição)
    @PostMapping
    public ResponseEntity<?> adicionarPrescricao(@RequestBody PrescricaoDTO dto) {
        Usuario usuario = getUsuarioLogado();

        ItemInventario item = inventarioRepository.findById(dto.getItemInventarioId())
                .orElse(null);

        if (item == null) {
            return ResponseEntity.badRequest().body("Item do inventário não encontrado.");
        }
        if (!item.getUsuario().getId().equals(usuario.getId())) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Item do inventário não pertence a este usuário.");
        }

        Prescricao novaPrescricao = new Prescricao();
        novaPrescricao.setUsuario(usuario); // Seta o usuário na prescrição
        novaPrescricao.setItemInventario(item); 
        novaPrescricao.setQuantidadePorDose(dto.getQuantidadePorDose()); 
        novaPrescricao.setDosagemPrescrita(dto.getDosagemPrescrita());
        novaPrescricao.setDataHoraInicio(dto.getDataHoraInicio());
        novaPrescricao.setIntervaloHoras(dto.getIntervaloHoras());
        novaPrescricao.setDuracaoDias(dto.getDuracaoDias());
        novaPrescricao.setInstrucoes(dto.getInstrucoes());
        novaPrescricao.setAtiva(true); 

        Prescricao prescricaoSalva = prescricaoRepository.save(novaPrescricao);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new PrescricaoResponseDTO(prescricaoSalva));
    }

    // PUT /api/prescricoes/{id} (Atualizar prescrição)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPrescricao(@PathVariable Long id, @RequestBody PrescricaoDTO dto) {
        Usuario usuario = getUsuarioLogado();

        Prescricao prescricao = prescricaoRepository.findById(id)
                .orElse(null);
        if (prescricao == null) {
            return ResponseEntity.notFound().build();
        }
        if (!prescricao.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Esta prescrição não pertence a você.");
        }

        ItemInventario item = inventarioRepository.findById(dto.getItemInventarioId())
                .orElse(null);
        if (item == null) {
            return ResponseEntity.badRequest().body("Item do inventário não encontrado.");
        }
        if (!item.getUsuario().getId().equals(usuario.getId())) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Item do inventário não pertence a este usuário.");
        }

        prescricao.setItemInventario(item);
        prescricao.setQuantidadePorDose(dto.getQuantidadePorDose());
        prescricao.setDosagemPrescrita(dto.getDosagemPrescrita());
        prescricao.setDataHoraInicio(dto.getDataHoraInicio());
        prescricao.setIntervaloHoras(dto.getIntervaloHoras());
        prescricao.setDuracaoDias(dto.getDuracaoDias());
        prescricao.setInstrucoes(dto.getInstrucoes());

        Prescricao prescricaoAtualizada = prescricaoRepository.save(prescricao);
        
        return ResponseEntity.ok(new PrescricaoResponseDTO(prescricaoAtualizada));
    }


    // DELETE /api/prescricoes/{id} (Deletar prescrição)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPrescricao(@PathVariable Long id) {
        Usuario usuario = getUsuarioLogado();

        Prescricao prescricao = prescricaoRepository.findById(id)
                .orElse(null);

        if (prescricao == null) {
            return ResponseEntity.notFound().build();
        }
        if (!prescricao.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Esta prescrição não pertence a você.");
        }

        prescricaoRepository.delete(prescricao);
        return ResponseEntity.ok().build();
    }
}

