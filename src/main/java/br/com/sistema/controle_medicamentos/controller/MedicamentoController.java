package br.com.sistema.controle_medicamentos.controller;

import br.com.sistema.controle_medicamentos.model.Medicamento;
import br.com.sistema.controle_medicamentos.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/medicamentos") // Este é o "Catálogo Global"
public class MedicamentoController {

    @Autowired 
    private MedicamentoRepository repository;

    // CREATE (Cadastrar no catálogo) - (Modificado)
    @PostMapping
    public Medicamento cadastrar(@RequestBody Medicamento medicamento) {
        // Lógica de 'quantidadeEstoque' removida
        return repository.save(medicamento);
    }

    // READ (Listar todos)
    @GetMapping
    public List<Medicamento> listarTodos() {
        return repository.findAll();
    }

    // READ (Buscar por ID)
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(medicamento -> ResponseEntity.ok(medicamento))
                .orElse(ResponseEntity.notFound().build()); 
    }

    // UPDATE (Atualizar catálogo) - (Modificado)
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> atualizar(@PathVariable Long id, @RequestBody Medicamento medicamentoDetails) {
        return repository.findById(id)
                .map(medicamento -> {
                    medicamento.setNome(medicamentoDetails.getNome());
                    medicamento.setLaboratorio(medicamentoDetails.getLaboratorio());
                    medicamento.setDosagem(medicamentoDetails.getDosagem());
                    // 'quantidadeEstoque' removida
                    medicamento.setViaAdministracao(medicamentoDetails.getViaAdministracao());
                    Medicamento atualizado = repository.save(medicamento);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE (Deletar do catálogo)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        // TODO: Verificar se este medicamento está em algum inventário antes de deletar?
        return repository.findById(id)
                .map(medicamento -> {
                    repository.delete(medicamento);
                    return ResponseEntity.ok().build(); 
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
