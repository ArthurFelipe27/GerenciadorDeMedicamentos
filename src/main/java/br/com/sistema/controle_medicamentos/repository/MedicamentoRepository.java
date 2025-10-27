package br.com.sistema.controle_medicamentos.repository;

import br.com.sistema.controle_medicamentos.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    // A Mágica do Spring Data JPA!
    // Você não precisa escrever NENHUM código aqui.
    // O JpaRepository já fornece métodos como:
    // save(), findById(), findAll(), deleteById(), etc.
} 
