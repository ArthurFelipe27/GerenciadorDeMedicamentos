package br.com.sistema.controle_medicamentos.controller;

import br.com.sistema.controle_medicamentos.model.Medicamento;
import br.com.sistema.controle_medicamentos.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Define que esta classe é um controlador REST
@RequestMapping("/api/medicamentos") // Todos os métodos aqui começarão com /api/medicamentos
public class MedicamentoController {

    @Autowired // Injeção de dependência: O Spring vai gerenciar o repositório
    private MedicamentoRepository repository;

    // CREATE (Cadastrar)
    @PostMapping
    public Medicamento cadastrar(@RequestBody Medicamento medicamento) {
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
                .map(medicamento -> ResponseEntity.ok(medicamento)) // Se achar, retorna 200 OK
                .orElse(ResponseEntity.notFound().build()); // Se não, retorna 404 Not Found
    }

    // UPDATE (Atualizar)
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> atualizar(@PathVariable Long id, @RequestBody Medicamento medicamentoDetails) {
        return repository.findById(id)
                .map(medicamento -> {
                    medicamento.setNome(medicamentoDetails.getNome());
                    medicamento.setLaboratorio(medicamentoDetails.getLaboratorio());
                    medicamento.setDosagem(medicamentoDetails.getDosagem());
                    medicamento.setQuantidadeEstoque(medicamentoDetails.getQuantidadeEstoque());
                    medicamento.setViaAdministracao(medicamentoDetails.getViaAdministracao());
                    Medicamento atualizado = repository.save(medicamento);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE (Deletar)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(medicamento -> {
                    repository.delete(medicamento);
                    return ResponseEntity.ok().build(); // Retorna 200 OK sem corpo
                })
                .orElse(ResponseEntity.notFound().build());
    }
}