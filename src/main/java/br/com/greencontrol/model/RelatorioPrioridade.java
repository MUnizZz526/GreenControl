package br.com.greencontrol.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "T_RELATORIO_PRIORIDADE")
public class RelatorioPrioridade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GC_RELATORIO")
    @SequenceGenerator(name = "SEQ_GC_RELATORIO", sequenceName = "SEQ_GC_RELATORIO", allocationSize = 1)
    @Column(name = "ID_RELATORIO")
    private Long id;

    @Column(name = "QT_URGENTE", nullable = false)
    private Integer qtUrgente;

    @Column(name = "QT_CRITICO", nullable = false)
    private Integer qtCritico;

    @Column(name = "QT_ATENCAO", nullable = false)
    private Integer qtAtencao;

    @Column(name = "QT_NORMAL", nullable = false)
    private Integer qtNormal;

    @Column(name = "RESUMO", nullable = false, length = 1000)
    private String resumo;

    @Column(name = "DATA_GERACAO", nullable = false)
    private LocalDateTime dataGeracao;

    public RelatorioPrioridade() {
    }

    public RelatorioPrioridade(Integer qtUrgente, Integer qtCritico, Integer qtAtencao, Integer qtNormal, String resumo) {
        this.qtUrgente = qtUrgente;
        this.qtCritico = qtCritico;
        this.qtAtencao = qtAtencao;
        this.qtNormal = qtNormal;
        this.resumo = resumo;
        this.dataGeracao = LocalDateTime.now();
    }

    public RelatorioPrioridade(Long id, Integer qtUrgente, Integer qtCritico, Integer qtAtencao, Integer qtNormal, String resumo, LocalDateTime dataGeracao) {
        this.id = id;
        this.qtUrgente = qtUrgente;
        this.qtCritico = qtCritico;
        this.qtAtencao = qtAtencao;
        this.qtNormal = qtNormal;
        this.resumo = resumo;
        this.dataGeracao = dataGeracao != null ? dataGeracao : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQtUrgente() {
        return qtUrgente;
    }

    public void setQtUrgente(Integer qtUrgente) {
        this.qtUrgente = qtUrgente;
    }

    public Integer getQtCritico() {
        return qtCritico;
    }

    public void setQtCritico(Integer qtCritico) {
        this.qtCritico = qtCritico;
    }

    public Integer getQtAtencao() {
        return qtAtencao;
    }

    public void setQtAtencao(Integer qtAtencao) {
        this.qtAtencao = qtAtencao;
    }

    public Integer getQtNormal() {
        return qtNormal;
    }

    public void setQtNormal(Integer qtNormal) {
        this.qtNormal = qtNormal;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelatorioPrioridade that = (RelatorioPrioridade) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RelatorioPrioridade{" +
                "id=" + id +
                ", qtUrgente=" + qtUrgente +
                ", qtCritico=" + qtCritico +
                ", qtAtencao=" + qtAtencao +
                ", qtNormal=" + qtNormal +
                ", resumo='" + resumo + '\'' +
                ", dataGeracao=" + dataGeracao +
                '}';
    }
}
