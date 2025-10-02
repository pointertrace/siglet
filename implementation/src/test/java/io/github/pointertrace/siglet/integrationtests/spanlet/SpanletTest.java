package io.github.pointertrace.siglet.integrationtests.spanlet;

class SpanletTest {

//    @Test
//    void simple() {
//
//
//        String config = """
//                receivers:
//                - debug: receiver
//                exporters:
//                - debug: exporter
//                pipelines:
//                - name: pipeline
//                  from: receiver
//                  start: spanlet
//                  processors:
//                  - prefix: spanlet
//                    to: exporter
//                    config:
//                      prefix: prefix-value-
//                """;
//
//        SigletConfig sigletConfig = new SigletConfig(
//                "prefix",
//                Location.of(1, 1),
//                "adds prefix to span name",
//                Location.of(1, 1),
//                PrefixSpanProcessor.class,
//                Location.of(1, 1),
//                new PrefixConfigNodeCheckerFactory().create(),
//                Location.of(1, 1), List.of());
//
//        Siglet siglet = new Siglet(config, List.of(sigletConfig));
//
//        siglet.start();
//
//        io.opentelemetry.proto.trace.v1.Span firstSpan = io.opentelemetry.proto.trace.v1.Span.newBuilder()
//                .setTraceId(AdapterUtils.traceId(0,1))
//                .setSpanId(AdapterUtils.spanId(1))
//                .setName("span-name")
//                .build();
//        Resource resource = Resource.newBuilder().build();
//        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
//        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
//        DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter);
//
//        siglet.stop();
//
//        List<ProtoSpanAdapter> exporter = DebugExporters.INSTANCE.get("exporter", ProtoSpanAdapter.class);
//        assertEquals(1, exporter.size());
//        assertEquals("prefix-value-span-name", exporter.getFirst().getName());
//    }
//
//    public record PrefixConfig(String prefix) implements Describable {
//
//        @Override
//        public String describe(int level) {
//            return "here should be the description of prefix config!";
//        }
//    }
//
//
//    public static class PrefixConfigBuilder implements NodeValueBuilder {
//
//        private String prefix;
//
//        public PrefixConfigBuilder prefix(String prefix) {
//            this.prefix = prefix;
//            return this;
//        }
//
//        public PrefixConfig build() {
//            return new PrefixConfig(prefix);
//        }
//
//    }
//
//    public static class PrefixSpanProcessor implements Spanlet<PrefixConfig> {
//
//        @Override
//        public Result span(Span span, ProcessorContext<PrefixConfig> prefixConfig,
//                           ResultFactory resultFactory) {
//            span.setName(prefixConfig.getConfig().prefix() + span.getName());
//            return resultFactory.proceed();
//        }
//
//    }
//
//    public static class PrefixConfigNodeCheckerFactory implements NodeCheckerFactory {
//
//        @Override
//        public NodeChecker create() {
//            return strictObject(PrefixConfigBuilder::new,
//                    requiredProperty(PrefixConfigBuilder::prefix, "prefix", text()));
//        }
//
//    }

}
