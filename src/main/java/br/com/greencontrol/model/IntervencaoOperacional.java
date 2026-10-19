package br.com.greencontrol.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "T_INTERVENCAO_OPERACIONAL")
public class IntervencaoOperacional {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GC_INTERVENCAO")
    @SequenceGenerator(name = "SEQ_GC_INTERVENCAO", sequenceName = "SEQ_GC_INTERVENCAO", allocationSize = 1)
    @Column(name = "ID_INTERVENCAO")
    private Long id;

    @Column(name = "TIPO_INTERVENCAO", nullable = false, length = 100)
    private String tipoIntervencao;

    @Column(name = "DATA_INTERVENCAO", nullable = false)
    private LocalDate dataIntervencao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TRECHO", nullable = false, foreignKey = @ForeignKey(name = "FK_INTERVENCAO_TRECHO"))
    private TrechoRodovia trecho;

    public IntervencaoOperacional() {
    }

    public IntervencaoOperacional(String tipoIntervencao, LocalDate dataIntervencao, TrechoRodovia trecho) {
        this.tipoIntervencao = tipoIntervencao;
        this.dataIntervencao = dataIntervencao != null ? dataIntervencao : LocalDate.now();
        this.trecho = trecho;
    }

    public IntervencaoOperacional(Long id, String tipoIntervencao, LocalDate dataIntervencao, TrechoRodovia trecho) {
        this.id = id;
        this.tipoIntervencao = tipoIntervencao;
        this.dataIntervencao = dataIntervencao != null ? dataIntervencao : LocalDate.now();
        this.trecho = trecho;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoIntervencao() {
        return tipoIntervencao;
    }

    public void setTipoIntervencao(String tipoIntervencao) {
        this.tipoIntervencao = tipoIntervencao;
    }

    public LocalDate getDataIntervencao() {
        return dataIntervencao;
    }

    public void setDataIntervencao(LocalDate dataIntervencao) {
        this.dataIntervencao = dataIntervencao;
    }

    public TrechoRodovia getTrecho() {
        return trecho;
    }

    public void setTrecho(TrechoRodovia trecho) {
        this.trecho = trecho;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntervencaoOperacional that = (IntervencaoOperacional) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IntervencaoOperacional{" +
                "id=" + id +
                ", tipoIntervencao='" + tipoIntervencao + '\'' +
                ", dataIntervencao=" + dataIntervencao +
                ", trechoId=" + (trecho != null ? trecho.getId() : "null") +
                '}';
    }
}
