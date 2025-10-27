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
    // CORREÇÃO: O caminho correto para o ID do usuário é 'p.usuario.id' (a ligação direta)
    // A ligação p.itemInventario.usuario.id também funcionaria, mas a ligação direta é mais limpa.
    @Query("SELECT p FROM Prescricao p WHERE p.usuario.id = :usuarioId AND p.ativa = true AND (p.dataHoraInicio <= :agora AND FUNCTION('DATE_ADD', p.dataHoraInicio, p.duracaoDias, 'DAY') > :agora)")
    List<Prescricao> findPrescricoesAtivas(@Param("usuarioId") Long usuarioId, @Param("agora") LocalDateTime agora);
    
}

