package br.com.greencontrol;

public class Main {
    public static void main(String[] args) {

        // --- TESTE 1: Instanciando trechos e Equipe ---
        TrechoRodovia trechoA = new TrechoRodovia(10, 20, 5.0);
        // Criando com valor negativo para testar sua validação:
        TrechoRodovia trechoB = new TrechoRodovia(50, 60, -10.0);

        EquipeManutencao equipeAlpha = new EquipeManutencao("Equipe Alpha", "Roçada Mecânica");

        // --- TESTE 2: Associação ---
        trechoB.setEquipeResponsavel(equipeAlpha);

        // --- TESTE 3: Comportamento (Crescimento) ---
        trechoA.registrarCrescimento(12.5); // Era 5.0, deve ir para 17.5

        // --- EXIBIÇÃO DOS RESULTADOS ---
        System.out.println("====== RELATÓRIO DE MONITORAMENTO ======");

        System.out.println("\nTRECHO A:");
        System.out.println("KM: " + trechoA.getQuilometroInicial() + " até " + trechoA.getQuilometroFinal());
        System.out.println("Vegetação: " + trechoA.getNivelVegetacao() + "cm");

        System.out.println("\nTRECHO B (Crítico):");
        System.out.println("KM: " + trechoB.getQuilometroInicial() + " até " + trechoB.getQuilometroFinal());
        System.out.println("Vegetação: " + trechoB.getNivelVegetacao() + "cm (Deveria ser 0 se o -10 foi barrado)");

        // Verificando se a associação funcionou
        if (trechoB.getEquipeResponsavel() != null) {
            System.out.println("Equipe Alocada: " + trechoB.getEquipeResponsavel().getNome());
            System.out.println("Especialidade: " + trechoB.getEquipeResponsavel().getEspecialidade());
        }

        System.out.println("\n========================================");
    }
}