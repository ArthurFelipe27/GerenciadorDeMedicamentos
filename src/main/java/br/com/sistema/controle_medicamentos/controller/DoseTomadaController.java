package br.com.sistema.controle_medicamentos.controller;

import br.com.sistema.controle_medicamentos.dto.DoseTomadaResponseDTO;
import br.com.sistema.controle_medicamentos.dto.ItemInventarioResponseDTO;
import br.com.sistema.controle_medicamentos.dto.RegistrarDoseDTO;
import br.com.sistema.controle_medicamentos.model.DoseTomada;
import br.com.sistema.controle_medicamentos.model.ItemInventario;
import br.com.sistema.controle_medicamentos.model.Prescricao;
import br.com.sistema.controle_medicamentos.model.Usuario;
import br.com.sistema.controle_medicamentos.repository.DoseTomadaRepository;
import br.com.sistema.controle_medicamentos.repository.ItemInventarioRepository; // *** CORREÇÃO: Importado ***
import br.com.sistema.controle_medicamentos.repository.PrescricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doses")
public class DoseTomadaController {

    @Autowired
    private DoseTomadaRepository doseTomadaRepository;

    @Autowired
    private PrescricaoRepository prescricaoRepository;

    // *** CORREÇÃO: Injetado o repositório do inventário para debitar o estoque ***
    @Autowired
    private ItemInventarioRepository itemInventarioRepository;

    // Endpoint para o front-end registrar uma dose (Atualizado)
    @PostMapping
    public ResponseEntity<?> registrarDose(@RequestBody RegistrarDoseDTO dto) {
        // Pega o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Busca a prescrição correspondente
        Prescricao prescricao = prescricaoRepository.findById(dto.getPrescricaoId())
                .orElse(null);

        if (prescricao == null) {
            return ResponseEntity.notFound().build();
        }

        // Verificação de segurança: O usuário logado é "dono" desta prescrição?
        if (!prescricao.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Esta prescrição não pertence a você.");
        }

        // Valida o status
        String status = dto.getStatus();
        if (status == null || (!status.equals("TOMADA") && !status.equals("PULADA"))) {
             return ResponseEntity.badRequest().body("Status inválido.");
        }

        // Cria e salva o registro da dose
        DoseTomada novaDose = new DoseTomada();
        novaDose.setPrescricao(prescricao);
        novaDose.setDataHoraTomada(dto.getDataHoraTomada());
        novaDose.setStatus(status); // Salva o status
        doseTomadaRepository.save(novaDose);

        // *** CORREÇÃO: Lógica de débito de estoque ***
        if (status.equals("TOMADA")) {
            // Busca o item do inventário associado a esta prescrição
            ItemInventario item = prescricao.getItemInventario();
            int quantidadeADebitar = prescricao.getQuantidadePorDose();
            
            // Debita o estoque
            int novoEstoque = item.getQuantidadeAtual() - quantidadeADebitar;
            // Garante que o estoque não fique negativo (embora o front-end deva travar)
            item.setQuantidadeAtual(Math.max(0, novoEstoque)); 
            
            // Salva a atualização do inventário
            ItemInventario itemSalvo = itemInventarioRepository.save(item);

            // Retorna o DTO do inventário atualizado (como o front-end espera)
            return ResponseEntity.status(HttpStatus.CREATED).body(new ItemInventarioResponseDTO(itemSalvo));
        } else {
            // Se foi "PULADA", apenas retorna 201 Created sem corpo
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    // Endpoint para o front-end buscar o relatório (Sem alterações)
    @GetMapping
    public ResponseEntity<List<DoseTomadaResponseDTO>> buscarRelatorioDoUsuario() {
        // Pega o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Busca no repositório usando o método que criamos
        List<DoseTomada> doses = doseTomadaRepository.findByPrescricao_Usuario_IdOrderByDataHoraTomadaDesc(usuario.getId());

        // Converte a lista de Entidades para a lista de DTOs
        List<DoseTomadaResponseDTO> dtos = doses.stream()
                .map(DoseTomadaResponseDTO::new) // Usa o construtor do DTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
