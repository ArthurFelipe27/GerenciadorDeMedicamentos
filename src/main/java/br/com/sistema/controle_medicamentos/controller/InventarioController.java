package br.com.sistema.controle_medicamentos.controller;

import br.com.sistema.controle_medicamentos.dto.ItemInventarioDTO;
import br.com.sistema.controle_medicamentos.dto.ItemInventarioResponseDTO;
import br.com.sistema.controle_medicamentos.model.ItemInventario;
import br.com.sistema.controle_medicamentos.model.Medicamento;
import br.com.sistema.controle_medicamentos.model.Usuario;
import br.com.sistema.controle_medicamentos.repository.ItemInventarioRepository;
import br.com.sistema.controle_medicamentos.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private ItemInventarioRepository inventarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    // GET /api/inventario (Lista o inventário do usuário)
    @GetMapping
    public ResponseEntity<List<ItemInventarioResponseDTO>> getInventarioUsuario() {
        Usuario usuario = getUsuarioLogado();
        List<ItemInventario> itens = inventarioRepository.findByUsuario_Id(usuario.getId());
        List<ItemInventarioResponseDTO> dtos = itens.stream()
                .map(ItemInventarioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST /api/inventario (Adiciona um item ao inventário)
    @PostMapping
    public ResponseEntity<?> adicionarItemInventario(@RequestBody ItemInventarioDTO dto) {
        Usuario usuario = getUsuarioLogado();
        
        Medicamento medicamento = medicamentoRepository.findById(dto.getMedicamentoId())
                .orElse(null);

        if (medicamento == null) {
            return ResponseEntity.badRequest().body("Medicamento do catálogo não encontrado.");
        }

        ItemInventario novoItem = new ItemInventario();
        novoItem.setUsuario(usuario);
        novoItem.setMedicamento(medicamento);
        novoItem.setQuantidadeAtual(dto.getQuantidadeAtual());
        novoItem.setLimiteAlerta(dto.getLimiteAlerta());
        novoItem.setDataValidade(dto.getDataValidade());

        ItemInventario itemSalvo = inventarioRepository.save(novoItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ItemInventarioResponseDTO(itemSalvo));
    }

    // PUT /api/inventario/{id} (Atualiza quantidade, limite, etc.)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarItemInventario(@PathVariable Long id, @RequestBody ItemInventarioDTO dto) {
        Usuario usuario = getUsuarioLogado();
        
        ItemInventario item = inventarioRepository.findById(id).orElse(null);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        if (!item.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Item não pertence a este usuário.");
        }

        // (Nota: Não permitimos mudar o medicamentoId, apenas os detalhes)
        item.setQuantidadeAtual(dto.getQuantidadeAtual());
        item.setLimiteAlerta(dto.getLimiteAlerta());
        item.setDataValidade(dto.getDataValidade());

        ItemInventario itemSalvo = inventarioRepository.save(item);
        return ResponseEntity.ok(new ItemInventarioResponseDTO(itemSalvo));
    }

    // DELETE /api/inventario/{id} (Remove item do inventário)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerItemInventario(@PathVariable Long id) {
         Usuario usuario = getUsuarioLogado();
        
        ItemInventario item = inventarioRepository.findById(id).orElse(null);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        if (!item.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Item não pertence a este usuário.");
        }

        // TODO: Verificar se este item está sendo usado em prescrições ativas antes de deletar?
        // (Por enquanto, vamos permitir a deleção)

        inventarioRepository.delete(item);
        return ResponseEntity.ok().build();
    }
}
