package io.github.pointertrace.siglet.impl.config.siglet.fatjar;

class FatJarSigletBundleLoaderTest {

//    private FatJarSigletBundleLoader fatJarSigletBundleLoader;
//
//    private File sigletJarFile;
//
//    private ProtoSpanAdapter spanAdapter;
//
//    private YamlParser yamlParser;
//
//    @BeforeEach
//    void setUp() {
//
//        sigletJarFile = ExampleJarsInfo.getFatJarExampleSigletFile();
//
//        fatJarSigletBundleLoader = new FatJarSigletBundleLoader();
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
//        SigletBundle sigletBundle = fatJarSigletBundleLoader.load(sigletJarFile);
//
//        assertEquals("fatjar:" + new JarFile(sigletJarFile).getName(),sigletBundle.id());
//
//        assertEquals(1, sigletBundle.definitions().size());
//
//        SigletDefinition sigletDefinition = sigletBundle.definitions().getFirst();
//
//        assertEquals("fatjar-suffix-spanlet", sigletDefinition.getSigletConfig().name());
//        assertEquals("adds a suffix to span name", sigletDefinition.getSigletConfig().description());
//        assertEquals("io.github.pointertrace.siglet.impl.test.bundle.jatjar.suffix.siglet.SuffixSpanlet",
//                sigletDefinition.getSigletConfig().sigletClassName());
//        assertEquals("io.github.pointertrace.siglet.impl.test.bundle.jatjar.suffix.parser.SuffixConfigCheckerFactory",
//                sigletDefinition.getSigletConfig().configCheckerFactoryClassName());
//        Spanlet<Object> spanlet = assertInstanceOf(Spanlet.class, sigletDefinition.createProcessor());
//        NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletDefinition.createConfigChecker());
//
//        Node node = yamlParser.parse("suffix: -suffix");
//        nodeChecker.check(node);
//        Object sigletConfig = node.getValue();
//
//        Context<Object> context = new ContextImpl<>(sigletConfig);
//
//        spanlet.span(spanAdapter, context, ResultFactoryImpl.INSTANCE);
//
//        assertEquals("span-name-suffix-fatjar", spanAdapter.getName());
//
//    }
//
//    @Test
//    void load_fileNotExists() {
//
//        sigletJarFile = new File("/invalid-file");
//
//        SigletError e = assertThrows(SigletError.class,() -> fatJarSigletBundleLoader.load(sigletJarFile));
//
//        assertEquals("File /invalid-file does not exist or is not a file", e.getMessage());
//    }
//
//    @Test
//    void load_jarIsNotSigletFatJar() {
//
//        sigletJarFile =
//                new File(CommandLine.class.getProtectionDomain().getCodeSource().getLocation().getFile());
//
//        assertNull(fatJarSigletBundleLoader.load(sigletJarFile));
//    }
}