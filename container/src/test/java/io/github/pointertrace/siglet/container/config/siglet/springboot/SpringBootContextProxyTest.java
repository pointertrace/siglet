package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.config.siglet.ExampleJarsInfo;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.net.protocol.Handlers;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


class SpringBootContextProxyTest {

    private SpringBootContextProxy springBootContextProxy;

    @BeforeEach
    void setUp() throws Exception {

        Handlers.register();
        File springBootJarFile = ExampleJarsInfo.getSpringBootExampleSigletFile();
        Archive archive = Archive.create(springBootJarFile);

        SpringBootClassLoader springBootClassLoader =
                SpringBootClassLoader.of(archive, SpringBootClassLoader.class.getClassLoader());

        springBootContextProxy = new SpringBootContextProxy(springBootClassLoader,
                SpringBootStartClassReader.read(springBootJarFile));

    }

    @Test
    void getProcessorAndNodeChecker() throws Exception {

        springBootContextProxy.start();

        assertInstanceOf(Spanlet.class, springBootContextProxy.getProcessor(
                "io.github.pointertrace.siglet.example.springboot.suffix.siglet.SuffixSpanlet"));

        assertInstanceOf(NodeCheckerFactory.class, springBootContextProxy.getNodeCheckerFactory(
                "io.github.pointertrace.siglet.example.springboot.suffix.parser.SuffixConfigChecker"));

        assertEquals("Hello World", getHelloWorldFromHttp());

        springBootContextProxy.stop();

        assertThrows(ConnectException.class, this::getHelloWorldFromHttp);

    }


    private String getHelloWorldFromHttp() throws IOException, InterruptedException {
        // Cria um HttpClient com timeout de 10 segundos
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {

            // Cria a requisição GET para a URL
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/hello"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            // Envia a requisição e obtém a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
    }
}