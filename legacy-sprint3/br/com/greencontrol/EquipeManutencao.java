package br.com.greencontrol;

public class EquipeManutencao {
    private String nome;
    private String especialidade;

    public EquipeManutencao(String nome,String especialidade){
        this.nome = nome;
        this.especialidade = especialidade;
    }
    public String getNome(){
        return nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }
}
