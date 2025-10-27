package br.com.sistema.controle_medicamentos.repository;

import br.com.sistema.controle_medicamentos.model.DoseTomada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoseTomadaRepository extends JpaRepository<DoseTomada, Long> {

    // Encontra todas as doses de um usuário específico, ordenadas pela mais recente
    // O Spring Data JPA entende a estrutura: findBy (Entidade)_(Campo)_(Campo)
    List<DoseTomada> findByPrescricao_Usuario_IdOrderByDataHoraTomadaDesc(Long usuarioId);
    
    // (Opcional) Encontra doses de uma prescrição específica
    List<DoseTomada> findByPrescricao_Id(Long prescricaoId);
}
