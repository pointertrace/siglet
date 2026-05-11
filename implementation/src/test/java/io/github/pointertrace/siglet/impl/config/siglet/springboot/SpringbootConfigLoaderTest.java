package io.github.pointertrace.siglet.impl.config.siglet.springboot;

import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;

import java.io.File;

class SpringbootConfigLoaderTest {

    private SpringBootBundleLoader springBootBundleLoader;

    private File sigletSpringBootFile;

    private ProtoSpanAdapter spanAdapter;

//    private YamlParser yamlParser;

//    @BeforeEach
//    void setUp() {
//
//        sigletSpringBootFile = ExampleJarsInfo.getSpringBootExampleSigletFile();
//
//        springBootBundleLoader = new SpringBootBundleLoader();
//
//        Span span = Span.newBuilder().setName("span-name").build();
//
//        spanAdapter = new ProtoSpanAdapter();
//        spanAdapter.recycle(span, null, null);
//
//        yamlParser = new YamlParser();
//    }
//
//    @Test
//    void load() throws IOException {
//
//        try (SigletBundle sigletBundle = springBootBundleLoader.load(sigletSpringBootFile)) {
//            assertTrue(sigletBundle.id().startsWith("springboot-uberjar:"));
//            assertTrue(sigletBundle.id().endsWith("springboot-suffix-spanlet-test.jar"));
//
//            assertEquals(1, sigletBundle.definitions().size());
//
//            SigletDefinition sigletDefinition = sigletBundle.definitions().getFirst();
//
//            assertEquals("springboot-suffix-spanlet", sigletDefinition.getSigletConfig().name());
//            assertEquals("adds a suffix to span name", sigletDefinition.getSigletConfig().description());
//            assertEquals("io.github.pointertrace.siglet.impl.test.bundle.springboot.suffix.siglet.SuffixSpanlet",
//                    sigletDefinition.getSigletConfig().sigletClassName());
//            assertEquals("io.github.pointertrace.siglet.impl.test.bundle.springboot.suffix.parser.SuffixConfigChecker",
//                    sigletDefinition.getSigletConfig().configCheckerFactoryClassName());
//            Spanlet<Object> spanlet = assertInstanceOf(Spanlet.class, sigletDefinition.createProcessor());
//            NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletDefinition.createConfigChecker());
//
//            Node node = yamlParser.parse("suffix: -suffix");
//            nodeChecker.check(node);
//            Object sigletConfig = node.getValue();
//
//            Context<Object> context = new ContextImpl<>(sigletConfig);
//
//            spanlet.span(spanAdapter, context, ResultFactoryImpl.INSTANCE);
//
//            assertEquals("span-name-suffix-springboot-uberjar", spanAdapter.getName());
//
//        }
//    }
//
//    @Test
//    void load_fileNotExists() {
//
//        sigletSpringBootFile = new File("/invalid-file");
//
//        SigletError e = assertThrows(SigletError.class, () -> springBootBundleLoader.load(sigletSpringBootFile));
//
//        assertEquals("File /invalid-file does not exists or is not a file", e.getMessage());
//    }
//
//    @Test
//    void load_jarIsNotSpringBootUberJar() {
//
//        sigletSpringBootFile =
//                new File(CommandLine.class.getProtectionDomain().getCodeSource().getLocation().getFile());
//
//        assertNull(springBootBundleLoader.load(sigletSpringBootFile));
//    }
}