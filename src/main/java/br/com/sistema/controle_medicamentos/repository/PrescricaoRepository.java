package br.com.sistema.controle_medicamentos.repository;

import br.com.sistema.controle_medicamentos.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {

    // Query customizada para buscar todas as prescrições de um usuário específico
    List<Prescricao> findByUsuarioId(Long usuarioId);

    // Query para buscar prescrições ativas de um usuário
    List<Prescricao> findByUsuarioIdAndAtiva(Long usuarioId, boolean ativa);
}
