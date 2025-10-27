package br.com.sistema.controle_medicamentos.config;

import br.com.sistema.controle_medicamentos.repository.UsuarioRepository;
import br.com.sistema.controle_medicamentos.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Define que esta classe é um componente gerenciado pelo Spring
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        var token = this.recoverToken(request);

        if (token != null) {
            // Se houver token, valida
            var username = tokenService.validarToken(token);
            UserDetails user = usuarioRepository.findByUsername(username).orElse(null);

            if (user != null) {
                // Se o usuário for válido, "avisa" o Spring Security
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // Continua a requisição (seja pública ou privada)
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extrair o token do Header "Authorization"
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        // O token vem como "Bearer <token>", removemos o "Bearer "
        return authHeader.replace("Bearer ", "");
    }
}