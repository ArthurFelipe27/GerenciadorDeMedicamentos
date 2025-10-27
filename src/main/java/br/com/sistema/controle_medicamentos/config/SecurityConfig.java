package br.com.sistema.controle_medicamentos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Importa a classe do Spring, sem conflito de nome agora
import org.springframework.security.web.SecurityFilterChain; 
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// A classe foi renomeada de "SecurityFilterChain" para "SecurityConfig"
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    // O @Bean agora retorna o tipo importado do Spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) 
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso público aos endpoints de login e registro
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        
                        // Libera o front-end estático
                        .requestMatchers("/*.html", "/*.js", "/*.css").permitAll()

                        // Protege todos os endpoints de medicamentos
                        .requestMatchers("/api/medicamentos/**").authenticated() 
                        
                        // Protege os endpoints de prescrições
                        .requestMatchers("/api/prescricoes/**").authenticated()
                        
                        // Exige autenticação para qualquer outra rota
                        .anyRequest().authenticated() 
                )
                // Adiciona nosso filtro JWT antes do filtro padrão do Spring
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
