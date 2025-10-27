package br.com.sistema.controle_medicamentos.repository;

import br.com.sistema.controle_medicamentos.model.ItemInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemInventarioRepository extends JpaRepository<ItemInventario, Long> {
    
    // Busca todos os itens de inventário de um usuário específico
    List<ItemInventario> findByUsuario_Id(Long usuarioId);
}
