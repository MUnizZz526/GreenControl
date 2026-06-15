package br.com.greencontrol;

public class TrechoRodovia implements MonitoravelViaIoT {
    private double quilometroInicial;
    private double quilometroFinal;
    private double nivelVegetacao;
    private EquipeManutencao equipeResponsavel;
    private String tipoClima;

    public TrechoRodovia(double quilometroInicial, double quilometroFinal, double nivelVegetacao, String tipoClima) {
        this.quilometroInicial = validarQuilometro(quilometroInicial);
        this.quilometroFinal = validarQuilometro(quilometroFinal);
        this.nivelVegetacao = validarNivelVegetacao(nivelVegetacao);
        this.tipoClima = tipoClima;
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

    public void simularCrescimentoDoPeriodo() {
        if (this.tipoClima != null && this.tipoClima.equalsIgnoreCase("Úmido")) {
            // Trecho úmido cresce mais rápido (ex: taxa de 8.0cm)
            this.registrarCrescimento(8.0);
        } else {
            // Trecho seco ou padrão cresce mais devagar (ex: taxa de 2.0cm)
            this.registrarCrescimento(2.0);
        }
    }

    public void registrarCrescimento(double taxa) {
        if (taxa < 0) {
            System.out.println("Erro! A taxa de crescimento deve ser positiva.");
        } else {
            this.nivelVegetacao += taxa;
        }
    }

    @Override
    public void transmitirDadosSensor() {
        this.simularCrescimentoDoPeriodo(); // Executa o crescimento dinâmico antes de transmitir
        System.out.println("[IoT] Dados enviados do KM " + quilometroInicial + ". Clima: " + tipoClima + " | Vegetação atual: " + this.getNivelVegetacao() + "cm");
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

    public String getTipoClima() {
        return tipoClima;
    }

    public void setTipoClima(String tipoClima) {
        this.tipoClima = tipoClima;
    }

    public EquipeManutencao getEquipeResponsavel() {
        return equipeResponsavel;
    }

    public void setEquipeResponsavel(EquipeManutencao equipeResponsavel) {
        this.equipeResponsavel = equipeResponsavel;
    }
}