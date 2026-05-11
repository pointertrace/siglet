package io.github.pointertrace.siglet.impl.config.siglet;

class SigletBundleTest {

//    private YamlParser yamlParser;
//
//    private ProtoSpanAdapter protoSpanAdapter;
//
//    @BeforeEach
//    void setUp() {
//
//        yamlParser = new YamlParser();
//
//        Span span = Span.newBuilder().setName("name").build();
//        protoSpanAdapter = new ProtoSpanAdapter();
//        protoSpanAdapter.recycle(span, null, null);
//    }
//
//
//    @Test
//    void load_fatjar() throws Exception {
//
//        try(SigletBundle sigletBundle = SigletBundle.load(ExampleJarsInfo.getFatJarExampleSigletFile())) {
//
//            assertNotNull(sigletBundle);
//            assertTrue(sigletBundle.id().startsWith("fatjar:"));
//            assertTrue(sigletBundle.id().endsWith("fatjar-suffix-spanlet-test.jar"));
//
//            assertNotNull(sigletBundle.definitions());
//            assertEquals(1, sigletBundle.definitions().size());
//            assertNotNull(sigletBundle.definitions().getFirst());
//
//            SigletConfig sigletConfig = (sigletBundle.definitions().getFirst().getSigletConfig());
//            assertEquals("fatjar-suffix-spanlet", sigletConfig.name());
//
//            assertNotNull(sigletBundle.definitions().getFirst().createProcessor());
//            Spanlet<?> spanlet = assertInstanceOf(Spanlet.class,
//                    sigletBundle.definitions().getFirst().createProcessor());
//
//            Node config = yamlParser.parse("suffix: -suffix");
//
//            NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletBundle.definitions().getFirst().createConfigChecker());
//            nodeChecker.check(config);
//
//            Object spanletConfig = config.getValue();
//            assertNotNull(sigletConfig);
//
//            ContextImpl<?> processorContext = new ContextImpl<>(spanletConfig);
//
//            spanlet.span(protoSpanAdapter, (Context) processorContext, ResultFactoryImpl.INSTANCE);
//
//            assertEquals("name-suffix-fatjar", protoSpanAdapter.getName());
//
//        }
//
//    }
//
//    @Test
//    void load_springBootUberJar() throws Exception {
//        try (SigletBundle sigletBundle = SigletBundle.load(ExampleJarsInfo.getSpringBootExampleSigletFile())) {
//
//            assertNotNull(sigletBundle);
//            assertTrue(sigletBundle.id().startsWith("springboot-uberjar:"));
//            assertTrue(sigletBundle.id().endsWith("springboot-suffix-spanlet-test.jar"));
//            assertNotNull(sigletBundle.definitions());
//            assertEquals(1, sigletBundle.definitions().size());
//            assertNotNull(sigletBundle.definitions().getFirst());
//
//            SigletConfig sigletConfig = (sigletBundle.definitions().getFirst().getSigletConfig());
//            assertEquals("springboot-suffix-spanlet", sigletConfig.name());
//
//            assertNotNull(sigletBundle.definitions().getFirst().createProcessor());
//            Spanlet<?> spanlet = assertInstanceOf(Spanlet.class,
//                    sigletBundle.definitions().getFirst().createProcessor());
//
//            Node config = yamlParser.parse("suffix: -suffix");
//
//            NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletBundle.definitions().getFirst().createConfigChecker());
//            nodeChecker.check(config);
//
//            Object spanletConfig = config.getValue();
//            assertNotNull(sigletConfig);
//
//            ContextImpl<?> processorContext = new ContextImpl<>(spanletConfig);
//
//            spanlet.span(protoSpanAdapter, (Context) processorContext, ResultFactoryImpl.INSTANCE);
//
//            assertEquals("name-suffix-springboot-uberjar", protoSpanAdapter.getName());
//
//        }
//    }
}