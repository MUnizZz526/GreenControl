package br.com.greencontrol.model;

import java.text.Normalizer;
import java.util.Locale;

/** Estratégias polimórficas da Sprint 2, independentes da persistência JPA. */
public enum EfeitoIntervencao {
    ROCADA { public double aplicar(double atual) { return 5.0; } },
    PULVERIZACAO { public double aplicar(double atual) { return Math.max(0, atual - 10); } },
    REGISTRO { public double aplicar(double atual) { return atual; } };

    public abstract double aplicar(double atual);

    public static EfeitoIntervencao de(String tipo) {
        String normalizado = Normalizer.normalize(tipo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        if (normalizado.contains("rocada")) return ROCADA;
        if (normalizado.contains("pulveriz")) return PULVERIZACAO;
        return REGISTRO;
    }
}
