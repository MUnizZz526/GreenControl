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
public class RelatorioPrioridadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve gerar relatório de prioridade via POST e consultar histórico via GET")
    void deveGerarRelatorioEListarHistorico() throws Exception {
        // 1. Cadastra trechos com diferentes alturas de vegetação
        String trechoUrgente = """
                {
                    "quilometroInicial": 0.0,
                    "quilometroFinal": 10.0,
                    "nivelVegetacao": 85.0,
                    "tipoClima": "Úmido"
                }
                """;
        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trechoUrgente))
                .andExpect(status().isCreated());

        String trechoNormal = """
                {
                    "quilometroInicial": 10.0,
                    "quilometroFinal": 20.0,
                    "nivelVegetacao": 10.0,
                    "tipoClima": "Seco"
                }
                """;
        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trechoNormal))
                .andExpect(status().isCreated());

        // 2. Dispara geração de relatório via POST /api/relatorios
        mockMvc.perform(post("/api/relatorios"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.qtUrgente").value(1))
                .andExpect(jsonPath("$.resumo").exists())
                .andExpect(jsonPath("$.dataGeracao").exists());

        // 3. Consulta histórico via GET /api/relatorios
        mockMvc.perform(get("/api/relatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
