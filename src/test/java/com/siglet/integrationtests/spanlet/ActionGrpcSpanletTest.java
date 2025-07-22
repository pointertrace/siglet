package com.siglet.integrationtests.spanlet;

import com.siglet.container.Siglet;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.engine.exporter.debug.DebugExporters;
import com.siglet.container.engine.receiver.debug.DebugReceivers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class ActionGrpcSpanletTest {

    private static ManagedChannel channel;
    private static OpenTelemetry openTelemetry;
    private static Tracer tracer;

    @BeforeEach
    void setUp() {
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317") // Endereço do coletor OpenTelemetry
                .build();

        // Configura o TracerProvider
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
                .build();

        // Inicializa o OpenTelemetry
        openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();

        // Obtém o tracer
        tracer = openTelemetry.getTracer("test-tracer");

        // Configura o canal gRPC
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    }

    @AfterAll
    public static void tearDown() {
        // Fecha o canal gRPC
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    @Test
    void test() throws Exception {

        String config = """
                receivers:
                - grpc: receiver
                  address: localhost:4317
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;


        Siglet siglet = new Siglet(config);

        siglet.start();

        generateSpan();

        Thread.sleep(300); // Simula latência

        siglet.stop();

        List<ProtoSpanAdapter> signals = DebugExporters.INSTANCE.get("exporter", ProtoSpanAdapter.class);
        assertEquals(1, signals.size());
        assertEquals("span-name-suffix", signals.getFirst().getName());

    }

    public void generateSpan() {
        Span span = tracer.spanBuilder("testGrpcCall").startSpan();

        try (Scope scope = span.makeCurrent()) {

            // Adiciona um evento ao span

            // Simula alguma lógica de negócio
            Thread.sleep(100); // Simula latência

            // Marca o span como concluído com sucesso
            span.setAttribute("grpc.status", "OK");
        } catch (InterruptedException e) {
            span.recordException(e);
            span.setAttribute("grpc.status", "ERROR");
        } finally {
            span.end();
        }
    }

}
