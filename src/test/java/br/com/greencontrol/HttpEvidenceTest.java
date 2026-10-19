package br.com.greencontrol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/** Servidor Tomcat real e banco isolado, sem reutilizar dados de outros testes. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:evidence;MODE=Oracle;DB_CLOSE_DELAY=-1")
class HttpEvidenceTest {
    @LocalServerPort int port;
    final HttpClient client=HttpClient.newHttpClient();
    final StringBuilder evidence=new StringBuilder("# Evidências HTTP reais — H2 isolado\n\nGeradas por HttpEvidenceTest usando Java HttpClient contra Tomcat em porta aleatória.\nOracle não foi utilizado. Horários e IDs são os retornados na execução.\n\n");

    String request(String method,String path,String body,int expected) throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
                .header("Content-Type","application/json");
        var response=client.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
        assertEquals(expected,response.statusCode(),method+" "+path+" "+response.body());
        evidence.append("## ").append(method).append(' ').append(path).append("\n\n```http\n")
                .append(method).append(' ').append(path).append('\n');
        if(body!=null) evidence.append(body).append('\n');
        evidence.append("HTTP ").append(response.statusCode()).append('\n').append(response.body()).append("\n```\n\n");
        return response.body();
    }

    @Test void capturarNoveRequisicoesReais() throws Exception {
        String body="{\"quilometroInicial\":0,\"quilometroFinal\":10,\"nivelVegetacao\":65,\"tipoClima\":\"Seco\"}";
        String created=request("POST","/api/trechos",body,201);
        long id=((Number)com.jayway.jsonpath.JsonPath.read(created,"$.id")).longValue();
        request("GET","/api/trechos/"+id,null,200);
        request("PUT","/api/trechos/"+id,body.replace("65","30"),200);
        request("POST","/api/relatorios",null,201);
        request("GET","/api/relatorios",null,200);
        String today=LocalDate.now().toString();
        request("GET","/api/relatorios/periodo?inicio="+today+"&fim="+today,null,200);
        request("DELETE","/api/trechos/"+id,null,204);
        request("POST","/api/trechos",body.replace("65","-1"),400);
        request("GET","/api/trechos/"+id,null,404);
        Path output=Path.of("docs/evidencias/http-h2.md");
        Files.createDirectories(output.getParent()); Files.writeString(output,evidence.toString());
    }
}
