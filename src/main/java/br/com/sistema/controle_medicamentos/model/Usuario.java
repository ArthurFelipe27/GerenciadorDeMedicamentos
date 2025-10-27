package br.com.sistema.controle_medicamentos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usuarios")
// As anotações @Data, @NoArgsConstructor, @AllArgsConstructor foram removidas
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) 
    private String username;

    private String password;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore 
    private Set<Prescricao> prescricoes;

    // --- CONSTRUTORES MANUAIS ---

    // Construtor vazio (necessário para o JPA)
    public Usuario() {
    }

    // Construtor para o AuthenticationController
    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Construtor completo
    public Usuario(Long id, String username, String password, Set<Prescricao> prescricoes) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.prescricoes = prescricoes;
    }

    // --- MÉTODOS DA INTERFACE USERDETAILS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Getters manuais que o UserDetails exige
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // --- GETTERS E SETTERS MANUAIS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Prescricao> getPrescricoes() {
        return prescricoes;
    }

    public void setPrescricoes(Set<Prescricao> prescricoes) {
        this.prescricoes = prescricoes;
    }

    // --- EQUALS E HASHCODE MANUAIS ---
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}

