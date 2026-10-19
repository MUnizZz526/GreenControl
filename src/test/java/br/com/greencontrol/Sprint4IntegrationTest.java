package br.com.greencontrol;

import br.com.greencontrol.model.*;
import br.com.greencontrol.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

/** Sem transação no teste: cada chamada HTTP efetivamente confirma sua transação. */
@SpringBootTest
@AutoConfigureMockMvc
class Sprint4IntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired TrechoRodoviaRepository trechos;
    @Autowired EquipeManutencaoRepository equipes;
    @Autowired IntervencaoOperacionalRepository intervencoes;
    @Autowired RelatorioPrioridadeRepository relatorios;

    @BeforeEach void limpar() {
        intervencoes.deleteAllInBatch(); trechos.deleteAllInBatch();
        equipes.deleteAllInBatch(); relatorios.deleteAllInBatch();
    }
    @AfterEach void limparDepois() { limpar(); }

    String trecho(double inicial, double fim, double vegetacao, String clima, Long equipe) {
        return "{\"quilometroInicial\":"+inicial+",\"quilometroFinal\":"+fim+
                ",\"nivelVegetacao\":"+vegetacao+",\"tipoClima\":\""+clima+"\",\"idEquipe\":"+equipe+"}";
    }
    long criarTrecho(double vegetacao) throws Exception {
        var r = mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON)
                .content(trecho(0,10,vegetacao,"Úmido",null))).andExpect(status().isCreated()).andReturn();
        return ((Number)com.jayway.jsonpath.JsonPath.read(r.getResponse().getContentAsString(),"$.id")).longValue();
    }
    String intervencao(String tipo, long id) {
        return "{\"tipoIntervencao\":\""+tipo+"\",\"idTrecho\":"+id+",\"dataIntervencao\":\"2026-10-19\"}";
    }

    @Test void crudTrechoCrescimentoEDerivedQueries() throws Exception {
        long id=criarTrecho(60);
        mvc.perform(get("/api/trechos/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.nivelPrioridade").value("CRITICO"));
        mvc.perform(get("/api/trechos")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(post("/api/trechos/"+id+"/simular-crescimento")).andExpect(status().isOk()).andExpect(jsonPath("$.nivelVegetacao").value(68));
        mvc.perform(get("/api/trechos/clima").param("tipo","úmido")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/trechos/vegetacao-minima").param("minimo","68")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/api/trechos/"+id).contentType(MediaType.APPLICATION_JSON).content(trecho(2,20,10,"Seco",null)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quilometroInicial").value(2)).andExpect(jsonPath("$.quilometroFinal").value(20)).andExpect(jsonPath("$.tipoClima").value("Seco"));
        mvc.perform(post("/api/trechos/"+id+"/simular-crescimento")).andExpect(jsonPath("$.nivelVegetacao").value(12));
        mvc.perform(delete("/api/trechos/"+id)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/trechos/"+id)).andExpect(status().isNotFound());
        mvc.perform(put("/api/trechos/"+id).contentType(MediaType.APPLICATION_JSON).content(trecho(0,10,10,"Seco",null))).andExpect(status().isNotFound());
        assertEquals(0,trechos.count());
    }

    @Test void crudEquipeEIntegridade() throws Exception {
        var r=mvc.perform(post("/api/equipes").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Alfa\",\"especialidade\":\"Roçada\"}"))
                .andExpect(status().isCreated()).andReturn();
        long id=((Number)com.jayway.jsonpath.JsonPath.read(r.getResponse().getContentAsString(),"$.id")).longValue();
        mvc.perform(get("/api/equipes/"+id)).andExpect(jsonPath("$.nome").value("Alfa"));
        mvc.perform(get("/api/equipes")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/equipes/buscar").param("especialidade","roç")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/api/equipes/"+id).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Beta\",\"especialidade\":\"Inspeção\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Beta")).andExpect(jsonPath("$.especialidade").value("Inspeção"));
        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON).content(trecho(0,10,20,"Seco",id))).andExpect(status().isCreated());
        mvc.perform(delete("/api/equipes/"+id)).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
        assertTrue(equipes.existsById(id));
        trechos.deleteAllInBatch();
        mvc.perform(delete("/api/equipes/"+id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/equipes/"+id)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/equipes/"+id)).andExpect(status().isNotFound());
        mvc.perform(put("/api/equipes/"+id).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"A\",\"especialidade\":\"B\"}")).andExpect(status().isNotFound());
    }

    @Test void crudIntervencaoSemReaplicarOuDesfazerEfeitoHistorico() throws Exception {
        long id=criarTrecho(80), outro=criarTrecho(40);
        var r=mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON).content(intervencao("Roçada Mecanizada",id)))
                .andExpect(status().isCreated()).andReturn();
        long registro=((Number)com.jayway.jsonpath.JsonPath.read(r.getResponse().getContentAsString(),"$.id")).longValue();
        assertEquals(5,trechos.findById(id).orElseThrow().getNivelVegetacao());
        mvc.perform(get("/api/intervencoes/"+registro)).andExpect(status().isOk());
        mvc.perform(get("/api/intervencoes")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/intervencoes/tipo").param("tipo","roçada mecanizada")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/intervencoes/trecho/"+id)).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete("/api/trechos/"+id)).andExpect(status().isConflict());
        mvc.perform(put("/api/intervencoes/"+registro).contentType(MediaType.APPLICATION_JSON).content(intervencao("Pulverizacao",outro)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idTrecho").value(outro)).andExpect(jsonPath("$.tipoIntervencao").value("Pulverizacao")).andExpect(jsonPath("$.dataIntervencao").value("2026-10-19"));
        assertEquals(40,trechos.findById(outro).orElseThrow().getNivelVegetacao());
        mvc.perform(delete("/api/intervencoes/"+registro)).andExpect(status().isNoContent());
        assertEquals(5,trechos.findById(id).orElseThrow().getNivelVegetacao());
        assertEquals(40,trechos.findById(outro).orElseThrow().getNivelVegetacao());
        mvc.perform(delete("/api/intervencoes/"+registro)).andExpect(status().isNotFound());
        mvc.perform(get("/api/intervencoes/"+registro)).andExpect(status().isNotFound());
        mvc.perform(put("/api/intervencoes/"+registro).contentType(MediaType.APPLICATION_JSON).content(intervencao("Inspeção",id))).andExpect(status().isNotFound());
    }

    @ParameterizedTest @CsvSource({"80,70","5,0","0,0"})
    void pulverizacaoRespeitaPiso(double inicial,double esperado) throws Exception {
        long id=criarTrecho(inicial);
        mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON).content(intervencao("Pulverização",id))).andExpect(status().isCreated());
        assertEquals(esperado,trechos.findById(id).orElseThrow().getNivelVegetacao());
    }

    @Test void referenciasInexistentesNaoGravam() throws Exception {
        mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON).content(intervencao("RocadaMecanizada",999999))).andExpect(status().isNotFound());
        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON).content(trecho(0,10,20,"Seco",999999L))).andExpect(status().isNotFound());
        assertEquals(0,intervencoes.count()); assertEquals(0,trechos.count());
    }

    @ParameterizedTest @CsvSource({"0,NORMAL","14.99,NORMAL","15,ATENCAO","30,ATENCAO","30.01,CRITICO","60,CRITICO","60.01,URGENTE"})
    void limitesPrioridade(double nivel,String esperado) throws Exception {
        long id=criarTrecho(nivel);
        mvc.perform(get("/api/trechos/"+id)).andExpect(jsonPath("$.nivelPrioridade").value(esperado));
    }

    @Test void totaisEHistoricoPersistidos() throws Exception {
        for(double n: new double[]{0,15,30,60,61}) criarTrecho(n);
        mvc.perform(post("/api/relatorios")).andExpect(status().isCreated()).andExpect(jsonPath("$.qtNormal").value(1))
                .andExpect(jsonPath("$.qtAtencao").value(2)).andExpect(jsonPath("$.qtCritico").value(1)).andExpect(jsonPath("$.qtUrgente").value(1));
        trechos.deleteAllInBatch();
        mvc.perform(get("/api/relatorios")).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].qtUrgente").value(1));
        assertEquals(1,relatorios.count());
    }

    @Test void periodoIncluiDiaFinalEPreservaHorario() throws Exception {
        for(String data: new String[]{"2026-10-14T00:00:00","2026-10-14T15:00:00","2026-10-14T23:59:59.999999","2026-10-15T00:00:00"}) {
            var r=new RelatorioPrioridade(0,0,0,0,"snapshot"); r.setDataGeracao(LocalDateTime.parse(data)); relatorios.saveAndFlush(r);
        }
        mvc.perform(get("/api/relatorios/periodo").param("inicio","2026-10-14").param("fim","2026-10-14")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        mvc.perform(get("/api/relatorios/periodo").param("inicio","2026-10-14T15:00:00").param("fim","2026-10-14T15:00:00")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/relatorios/periodo").param("inicio","2020-01-01").param("fim","2020-01-01")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @ParameterizedTest @CsvSource({"2026-02-30,2026-10-14","lixo,2026-10-14","2026-10-15,2026-10-14","2026-10-14T15:00:00,2026-10-14T14:00:00"})
    void periodoInvalidoRetorna400(String inicio,String fim) throws Exception {
        mvc.perform(get("/api/relatorios/periodo").param("inicio",inicio).param("fim",fim)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test void entradasInvalidasSao400() throws Exception {
        for(String body:new String[]{"{",trecho(0,10,-1,"Seco",null),trecho(-1,10,0,"Seco",null),trecho(10,10,0,"Seco",null),trecho(0,1000000,0,"Seco",null),trecho(0,10,0,"X".repeat(51),null),trecho(0,10,0,"Seco",0L)})
            mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/trechos/abc")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/trechos/0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/trechos/vegetacao-minima").param("minimo","-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/equipes/buscar")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/periodo").param("inicio","")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/equipes").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"\",\"especialidade\":\"\"}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON).content(intervencao("X".repeat(101),1))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON).content("{\"tipoIntervencao\":\"Inspeção\",\"idTrecho\":0}")).andExpect(status().isBadRequest());
        assertEquals(0,trechos.count());
    }

    @Test void sequencesGeramIdsDistintosAposExclusao() throws Exception {
        long a=criarTrecho(10); mvc.perform(delete("/api/trechos/"+a)).andExpect(status().isNoContent());
        long b=criarTrecho(10); assertTrue(b>a);
        for(Class<?> c:new Class<?>[]{TrechoRodovia.class,EquipeManutencao.class,IntervencaoOperacional.class,RelatorioPrioridade.class})
            assertEquals(1,c.getDeclaredField("id").getAnnotation(jakarta.persistence.SequenceGenerator.class).allocationSize());
    }

    @Test void erro500NaoExibeDetalheInterno() {
        var resposta=new br.com.greencontrol.exception.GlobalExceptionHandler().handleGenerico(new RuntimeException("SQL secret password"));
        assertEquals(500,resposta.getStatusCode().value()); assertFalse(resposta.getBody().mensagem().contains("secret"));
    }

    @Test void textoMultibyteRespeitaVarcharOracle() throws Exception {
        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON).content(trecho(0,10,0,"á".repeat(26),null)))
                .andExpect(status().isBadRequest());
    }

    @Test void falhaNaAtualizacaoNaoAlteraVegetacaoNemRegistro() throws Exception {
        long id=criarTrecho(80);
        var r=mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON).content(intervencao("Inspeção",id))).andExpect(status().isCreated()).andReturn();
        long registro=((Number)com.jayway.jsonpath.JsonPath.read(r.getResponse().getContentAsString(),"$.id")).longValue();
        mvc.perform(put("/api/intervencoes/"+registro).contentType(MediaType.APPLICATION_JSON).content(intervencao("Pulverizacao",999999)))
                .andExpect(status().isNotFound());
        assertEquals("Inspeção",intervencoes.findById(registro).orElseThrow().getTipoIntervencao());
        assertEquals(80,trechos.findById(id).orElseThrow().getNivelVegetacao());
        mvc.perform(put("/api/trechos/"+id).contentType(MediaType.APPLICATION_JSON).content(trecho(0,10,5,"Seco",999999L)))
                .andExpect(status().isNotFound());
        assertEquals(80,trechos.findById(id).orElseThrow().getNivelVegetacao());
    }

    @Test void crescimentoExcessivoRetorna400SemPersistir() throws Exception {
        long id=criarTrecho(999999.99);
        mvc.perform(post("/api/trechos/"+id+"/simular-crescimento")).andExpect(status().isBadRequest());
        assertEquals(999999.99,trechos.findById(id).orElseThrow().getNivelVegetacao());
    }
}
