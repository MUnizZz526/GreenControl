package br.com.greencontrol;

import br.com.greencontrol.model.*;
import br.com.greencontrol.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/** Compatibilidade de IDs explícitos com tabelas BY DEFAULT AS IDENTITY e dados existentes.
 * H2 não executa PL/SQL; a migração Oracle permanece sujeita à homologação externa. */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:compatibility;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=none", "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:identity-compatibility.sql"})
class IdentitySequenceCompatibilityTest {
    @Autowired EquipeManutencaoRepository equipes;
    @Autowired TrechoRodoviaRepository trechos;
    @Autowired IntervencaoOperacionalRepository intervencoes;
    @Autowired RelatorioPrioridadeRepository relatorios;

    @Test void preservarIdsExistentesEGerarIdsAcimaDoMaximo() {
        var equipe=equipes.saveAndFlush(new EquipeManutencao("Nova","Inspeção"));
        var trecho=new TrechoRodovia(0.0,10.0,20.0,"Seco"); trecho.setEquipe(equipe);
        trecho=trechos.saveAndFlush(trecho);
        var intervencao=intervencoes.saveAndFlush(new IntervencaoOperacional("Inspeção",null,trecho));
        var relatorio=relatorios.saveAndFlush(new RelatorioPrioridade(0,0,1,0,"Novo"));
        for(long id:new long[]{equipe.getId(),trecho.getId(),intervencao.getId(),relatorio.getId()}) assertTrue(id>100);
        assertTrue(equipes.existsById(100L)); assertTrue(trechos.existsById(100L));
        assertTrue(intervencoes.existsById(100L)); assertTrue(relatorios.existsById(100L));
    }
}
