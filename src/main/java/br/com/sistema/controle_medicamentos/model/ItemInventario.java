package br.com.sistema.controle_medicamentos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventario_pessoal")
public class ItemInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento; // A definição (do catálogo)

    @Column(nullable = false)
    private int quantidadeAtual; // O que eu tenho

    @Column(nullable = false)
    private int limiteAlerta; // Avisar quando <= N

    private LocalDate dataValidade; // Opcional, mas útil

    // --- Construtores ---
    public ItemInventario() {}

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(int quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public int getLimiteAlerta() {
        return limiteAlerta;
    }

    public void setLimiteAlerta(int limiteAlerta) {
        this.limiteAlerta = limiteAlerta;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }
}
