package br.com.sistema.controle_medicamentos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***

@Entity
@Table(name = "doses_tomadas")
public class DoseTomada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescricao_id", nullable = false)
    private Prescricao prescricao;

    @JoinColumn(nullable = false)
    private Instant dataHoraTomada; // *** CORREÇÃO: Mudado de LocalDateTime para Instant ***

    @JoinColumn(nullable = false)
    private String status; 

    // --- Construtores (Sem Lombok) ---
    public DoseTomada() {
    }

    public DoseTomada(Prescricao prescricao, Instant dataHoraTomada, String status) { // *** CORREÇÃO: Instant ***
        this.prescricao = prescricao;
        this.dataHoraTomada = dataHoraTomada;
        this.status = status;
    }

    // --- Getters e Setters (Sem Lombok) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prescricao getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(Prescricao prescricao) {
        this.prescricao = prescricao;
    }

    public Instant getDataHoraTomada() { // *** CORREÇÃO: Instant ***
        return dataHoraTomada;
    }

    public void setDataHoraTomada(Instant dataHoraTomada) { // *** CORREÇÃO: Instant ***
        this.dataHoraTomada = dataHoraTomada;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
