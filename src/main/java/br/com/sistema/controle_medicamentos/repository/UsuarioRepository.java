package br.com.sistema.controle_medicamentos.repository;

import br.com.sistema.controle_medicamentos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método que o Spring Security usará para buscar o usuário pelo username
    Optional<UserDetails> findByUsername(String username);
}
