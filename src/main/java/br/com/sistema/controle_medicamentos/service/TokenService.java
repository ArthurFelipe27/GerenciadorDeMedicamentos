package br.com.sistema.controle_medicamentos.service;

import br.com.sistema.controle_medicamentos.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}") // Pega a chave do application.properties
    private String secret;

    // Método para GERAR o token no login
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("api-controle-medicamentos") // Identificador da sua API
                    .withSubject(usuario.getUsername()) // O "dono" do token
                    .withExpiresAt(getExpirationDate()) // Data de expiração
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    // Método para VALIDAR o token em cada requisição
    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("api-controle-medicamentos")
                    .build()
                    .verify(token) // Verifica a assinatura e a validade
                    .getSubject(); // Retorna o username (subject) se o token for válido
        } catch (JWTVerificationException exception) {
            return ""; // Retorna vazio se o token for inválido
        }
    }

    private Instant getExpirationDate() {
        // Token expira em 2 horas (pode ajustar)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
