package br.com.sistema.controle_medicamentos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "prescricoes")
// As anotações @Data, @NoArgsConstructor, @AllArgsConstructor foram removidas
public class Prescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private String dosagemPrescrita; 

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio; 

    @Column(nullable = false)
    private int intervaloHoras; 

    @Column(nullable = false)
    private int duracaoDias; 

    private String instrucoes; 

    private boolean ativa; 

    // --- CONSTRUTORES MANUAIS ---

    public Prescricao() {
    }

    public Prescricao(Long id, Usuario usuario, Medicamento medicamento, String dosagemPrescrita, LocalDateTime dataHoraInicio, int intervaloHoras, int duracaoDias, String instrucoes, boolean ativa) {
        this.id = id;
        this.usuario = usuario;
        this.medicamento = medicamento;
        this.dosagemPrescrita = dosagemPrescrita;
        this.dataHoraInicio = dataHoraInicio;
        this.intervaloHoras = intervaloHoras;
        this.duracaoDias = duracaoDias;
        this.instrucoes = instrucoes;
        this.ativa = ativa;
    }

    // --- GETTERS E SETTERS MANUAIS ---

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

    public String getDosagemPrescrita() {
        return dosagemPrescrita;
    }

    public void setDosagemPrescrita(String dosagemPrescrita) {
        this.dosagemPrescrita = dosagemPrescrita;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
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

    // --- EQUALS E HASHCODE MANUAIS ---
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescricao that = (Prescricao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

