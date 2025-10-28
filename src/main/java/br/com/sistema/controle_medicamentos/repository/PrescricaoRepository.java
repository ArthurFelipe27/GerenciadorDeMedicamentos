package br.com.sistema.controle_medicamentos.repository;

import br.com.sistema.controle_medicamentos.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {

    // CORREÇÃO: Alterado de findByUsuario_Id para findByUsuarioId
    // Spring Data JPA entenderá que deve buscar por prescricao.usuario.id
    List<Prescricao> findByUsuarioId(Long usuarioId);

    // Query para encontrar prescrições ativas
    // CORREÇÃO: O uso da função 'TIMESTAMPDIFF' falhou na validação do Hibernate (tipo de argumento).
    // Voltamos a usar a lógica de 'DATE_ADD' (específica do MySQL), mas agora
    // usando uma 'nativeQuery = true' para garantir que a sintaxe SQL
    // 'DATE_ADD(data, INTERVAL expr UNIT)' seja executada corretamente.
    // Também ajustamos os nomes das colunas para o padrão snake_case (ex: data_hora_inicio).
    @Query(value = "SELECT * FROM prescricoes p WHERE p.usuario_id = :usuarioId AND p.ativa = true AND " +
           "p.data_hora_inicio <= :agora AND " +
           "DATE_ADD(p.data_hora_inicio, INTERVAL p.duracao_dias DAY) > :agora",
           nativeQuery = true)
    List<Prescricao> findPrescricoesAtivas(@Param("usuarioId") Long usuarioId, @Param("agora") LocalDateTime agora);
    
}

