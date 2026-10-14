package br.com.greencontrol.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroRespostaDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        List<String> detalhes
) {
    public ErroRespostaDTO(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem, List.of());
    }

    public ErroRespostaDTO(int status, String erro, String mensagem, List<String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, detalhes);
    }
}
