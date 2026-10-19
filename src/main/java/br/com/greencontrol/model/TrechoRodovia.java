package br.com.greencontrol.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "T_TRECHO_RODOVIA")
public class TrechoRodovia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GC_TRECHO")
    @SequenceGenerator(name = "SEQ_GC_TRECHO", sequenceName = "SEQ_GC_TRECHO", allocationSize = 1)
    @Column(name = "ID_TRECHO")
    private Long id;

    @Column(name = "QUILOMETRO_INICIAL", nullable = false)
    private Double quilometroInicial;

    @Column(name = "QUILOMETRO_FINAL", nullable = false)
    private Double quilometroFinal;

    @Column(name = "NIVEL_VEGETACAO", nullable = false)
    private Double nivelVegetacao;

    @Column(name = "TIPO_CLIMA", nullable = false, length = 50)
    private String tipoClima;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_EQUIPE", foreignKey = @ForeignKey(name = "FK_TRECHO_EQUIPE"))
    private EquipeManutencao equipe;

    public TrechoRodovia() {
    }

    public TrechoRodovia(Double quilometroInicial, Double quilometroFinal, Double nivelVegetacao, String tipoClima) {
        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;
        this.nivelVegetacao = nivelVegetacao;
        this.tipoClima = tipoClima;
    }

    public TrechoRodovia(Long id, Double quilometroInicial, Double quilometroFinal, Double nivelVegetacao, String tipoClima, EquipeManutencao equipe) {
        this.id = id;
        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;
        this.nivelVegetacao = nivelVegetacao;
        this.tipoClima = tipoClima;
        this.equipe = equipe;
    }

    // Regras de domínio herdadas da Sprint 1 e 2
    public void simularCrescimentoDoPeriodo() {
        if (this.tipoClima != null && this.tipoClima.equalsIgnoreCase("Úmido")) {
            this.registrarCrescimento(8.0);
        } else {
            this.registrarCrescimento(2.0);
        }
    }

    public void registrarCrescimento(double taxa) {
        if (taxa > 0) {
            this.nivelVegetacao = (this.nivelVegetacao != null ? this.nivelVegetacao : 0.0) + taxa;
        }
    }

    public String obterNivelPrioridade() {
        if (this.nivelVegetacao == null) return "NORMAL";
        if (this.nivelVegetacao > 60.0) return "URGENTE";
        if (this.nivelVegetacao > 30.0) return "CRITICO";
        if (this.nivelVegetacao >= 15.0) return "ATENCAO";
        return "NORMAL";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuilometroInicial() {
        return quilometroInicial;
    }

    public void setQuilometroInicial(Double quilometroInicial) {
        this.quilometroInicial = quilometroInicial;
    }

    public Double getQuilometroFinal() {
        return quilometroFinal;
    }

    public void setQuilometroFinal(Double quilometroFinal) {
        this.quilometroFinal = quilometroFinal;
    }

    public Double getNivelVegetacao() {
        return nivelVegetacao;
    }

    public void setNivelVegetacao(Double nivelVegetacao) {
        this.nivelVegetacao = nivelVegetacao;
    }

    public String getTipoClima() {
        return tipoClima;
    }

    public void setTipoClima(String tipoClima) {
        this.tipoClima = tipoClima;
    }

    public EquipeManutencao getEquipe() {
        return equipe;
    }

    public void setEquipe(EquipeManutencao equipe) {
        this.equipe = equipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrechoRodovia that = (TrechoRodovia) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TrechoRodovia{" +
                "id=" + id +
                ", quilometroInicial=" + quilometroInicial +
                ", quilometroFinal=" + quilometroFinal +
                ", nivelVegetacao=" + nivelVegetacao +
                ", tipoClima='" + tipoClima + '\'' +
                ", equipe=" + (equipe != null ? equipe.getNome() : "null") +
                '}';
    }
}
