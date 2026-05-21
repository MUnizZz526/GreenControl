package br.com.greencontrol;

public class TrechoRodovia {
    private double quilometroInicial;
    private double quilometroFinal;
    private double nivelVegetacao;
    private EquipeManutencao equipeResponsavel;

    public TrechoRodovia(double quilometroInicial, double quilometroFinal, double nivelVegetacao) {
        this.quilometroInicial = validarQuilometro(quilometroInicial);
        this.quilometroFinal = validarQuilometro(quilometroFinal);
        this.nivelVegetacao = validarNivelVegetacao(nivelVegetacao);
    }

    private double validarQuilometro(double quilometro) {
        if (quilometro < 0) {
            System.out.println("Erro: Quilometragem não pode ser negativa!");
            return 0;
        }
        return quilometro;
    }

    private double validarNivelVegetacao(double nivel) {
        if (nivel < 0) {
            System.out.println("Erro: Nível de vegetação não pode ser negativo!");
            return 0;
        }
        return nivel;
    }

    public double getQuilometroInicial() {
        return quilometroInicial;
    }

    public void setQuilometroInicial(double quilometroInicial) {
        this.quilometroInicial = validarQuilometro(quilometroInicial);
    }

    public double getQuilometroFinal() {
        return quilometroFinal;
    }

    public void setQuilometroFinal(double quilometroFinal) {
        this.quilometroFinal = validarQuilometro(quilometroFinal);
    }

    public double getNivelVegetacao() {
        return nivelVegetacao;
    }

    public void setNivelVegetacao(double nivel) {
        this.nivelVegetacao = validarNivelVegetacao(nivel);
    }

    // Getter e Setter para a Associação
    public EquipeManutencao getEquipeResponsavel() {
        return equipeResponsavel;
    }

    public void setEquipeResponsavel(EquipeManutencao equipeResponsavel) {
        this.equipeResponsavel = equipeResponsavel;
    }

    public void registrarCrescimento(double taxa) {
        if (taxa < 0) {
            System.out.println("Erro! A taxa de crescimento deve ser positiva.");
        } else {
            this.nivelVegetacao += taxa;
        }
    }
}