package br.com.greencontrol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.transaction.annotation.Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class TrechoRodoviaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve criar um novo trecho com sucesso (HTTP 201)")
    void deveCriarTrechoComSucesso() throws Exception {
        String json = """
                {
                    "quilometroInicial": 0.0,
                    "quilometroFinal": 15.0,
                    "nivelVegetacao": 45.0,
                    "tipoClima": "Úmido"
                }
                """;

        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quilometroInicial").value(0.0))
                .andExpect(jsonPath("$.quilometroFinal").value(15.0))
                .andExpect(jsonPath("$.nivelVegetacao").value(45.0))
                .andExpect(jsonPath("$.nivelPrioridade").value("CRITICO"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 ao tentar cadastrar trecho com vegetação negativa")
    void deveRetornarErroValidacaoVegetacaoNegativa() throws Exception {
        String json = """
                {
                    "quilometroInicial": 10.0,
                    "quilometroFinal": 20.0,
                    "nivelVegetacao": -5.0,
                    "tipoClima": "Seco"
                }
                """;

        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 quando quilômetro final for menor que o inicial")
    void deveRetornarErroKmFinalMenorQueInicial() throws Exception {
        String json = """
                {
                    "quilometroInicial": 50.0,
                    "quilometroFinal": 20.0,
                    "nivelVegetacao": 10.0,
                    "tipoClima": "Seco"
                }
                """;

        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O quilômetro final (20.0) deve ser estritamente maior que o quilômetro inicial (50.0)."));
    }

    @Test
    @DisplayName("Deve retornar HTTP 404 para busca de trecho inexistente")
    void deveRetornar404ParaTrechoInexistente() throws Exception {
        mockMvc.perform(get("/api/trechos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Deve listar todos os trechos cadastrados (HTTP 200)")
    void deveListarTrechos() throws Exception {
        mockMvc.perform(get("/api/trechos"))
                .andExpect(status().isOk());
    }
}
