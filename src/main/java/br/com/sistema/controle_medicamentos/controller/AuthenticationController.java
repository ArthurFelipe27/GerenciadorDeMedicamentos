package br.com.sistema.controle_medicamentos.controller;

// Se o arquivo "LoginDTO.java" estiver com o nome correto,
// esta linha NÃO dará mais erro.
import br.com.sistema.controle_medicamentos.dto.LoginDTO; 
import br.com.sistema.controle_medicamentos.dto.LoginResponseDTO;
import br.com.sistema.controle_medicamentos.dto.RegisterDTO;
import br.com.sistema.controle_medicamentos.model.Usuario;
import br.com.sistema.controle_medicamentos.repository.UsuarioRepository;
import br.com.sistema.controle_medicamentos.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    // O tipo "LoginDTO" agora será resolvido
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO data) { 
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO data) {
        if (this.repository.findByUsername(data.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já cadastrado.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        
        Usuario novoUsuario = new Usuario(data.username(), encryptedPassword);
        this.repository.save(novoUsuario);

        return ResponseEntity.ok().body("Usuário cadastrado com sucesso.");
    }
}

