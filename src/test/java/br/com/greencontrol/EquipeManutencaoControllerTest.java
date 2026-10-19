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
public class EquipeManutencaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve executar o ciclo completo de CRUD de Equipe de Manutenção")
    void deveExecutarCrudEquipe() throws Exception {
        // 1. Criar Equipe (POST)
        String equipeJson = """
                {
                    "nome": "Equipe Gamma",
                    "especialidade": "Roçada Mecanizada"
                }
                """;

        mockMvc.perform(post("/api/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Equipe Gamma"))
                .andExpect(jsonPath("$.especialidade").value("Roçada Mecanizada"));

        // 2. Listar Equipes (GET)
        mockMvc.perform(get("/api/equipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 3. Buscar por especialidade (GET)
        mockMvc.perform(get("/api/equipes/buscar").param("especialidade", "Roçada"))
                .andExpect(status().isOk());
    }
}
