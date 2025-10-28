package br.com.sistema.controle_medicamentos.model;

import jakarta.persistence.*;
import java.time.Instant; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***

@Entity
@Table(name = "prescricoes")
public class Prescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_inventario_id", nullable = false)
    private ItemInventario itemInventario; 
    
    @Column(nullable = false)
    private int quantidadePorDose; 

    private String dosagemPrescrita; 
    private Instant dataHoraInicio; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***
    private int intervaloHoras;
    private int duracaoDias;
    private String instrucoes;
    private boolean ativa;

    // --- Construtores ---
    public Prescricao() {}
    
    // --- Getters e Setters (Manuais) ---
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
    
    public String getDosagemPrescrita() {
        return dosagemPrescrita;
    }
    public void setDosagemPrescrita(String dosagemPrescrita) {
        this.dosagemPrescrita = dosagemPrescrita;
    }
    public Instant getDataHoraInicio() { // *** CORREÇÃO: Instant ***
        return dataHoraInicio;
    }
    public void setDataHoraInicio(Instant dataHoraInicio) { // *** CORREÇÃO: Instant ***
        this.dataHoraInicio = dataHoraInicio;
    }
    public int getIntervaloHoras() {
        return intervaloHoras;
    }
    public void setIntervaloHoras(int intervaloHoras) {
        this.intervaloHoras = intervaloHoras;
    }
    public int getDuracaoDias() {
        return duracaoDias;
    }
    public void setDuracaoDias(int duracaoDias) {
        this.duracaoDias = duracaoDias;
    }
    public String getInstrucoes() {
        return instrucoes;
    }
    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
    }
    public boolean isAtiva() {
        return ativa;
    }
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
    
    public ItemInventario getItemInventario() {
        return itemInventario;
    }
    public void setItemInventario(ItemInventario itemInventario) {
        this.itemInventario = itemInventario;
    }
    public int getQuantidadePorDose() {
        return quantidadePorDose;
    }
    public void setQuantidadePorDose(int quantidadePorDose) {
        this.quantidadePorDose = quantidadePorDose;
    }
}
