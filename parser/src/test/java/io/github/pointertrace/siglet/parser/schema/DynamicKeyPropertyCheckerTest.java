package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DynamicKeyPropertyCheckerTest {

    private ObjectChecker objectChecker;

    private Set<String> keys;

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }


    @Test
    void check_required() {
        keys = new HashSet<>(Arrays.asList("key1", "key2"));

        objectChecker = new ObjectChecker(Bean::new, true,
                new DynamicKeyPropertyChecker(Bean::setKey, Bean::setKeyLocation, Bean::setValue,
                        Bean::setValueLocation, keys, true, new TextChecker()));

        String yaml = "key1: value1";

        Node root = parser.parse(yaml);

        objectChecker.check(root);

        Bean bean = root.getValue(Bean.class);

        assertNotNull(bean);

        assertEquals("key1", bean.getKey());
        assertEquals(Location.of(1, 1), bean.getKeyLocation());
        assertEquals("value1", bean.getValue());
        assertEquals(Location.of(1, 7), bean.getValueLocation());

    }


    @Test
    void check_notRequiredNotFound() {
        keys = new HashSet<>(Arrays.asList("key1", "key2"));

        objectChecker = new ObjectChecker(Bean::new, false,
                new DynamicKeyPropertyChecker(Bean::setKey, Bean::setKeyLocation, Bean::setValue,
                        Bean::setValueLocation, keys, false, new TextChecker()));

        String yaml = "key-3: value1\n" +
                      "key-4: value1\n";

        Node root = parser.parse(yaml);

        assertDoesNotThrow(() -> objectChecker.check(root));

    }

    @Test
    void check_requiredNotFound() {

        keys = new HashSet<>(Arrays.asList("key1", "key2"));

        objectChecker = new ObjectChecker(Bean::new, true,
                new DynamicKeyPropertyChecker(Bean::setKey, Bean::setKeyLocation, Bean::setValue,
                        Bean::setValueLocation, keys, true, new TextChecker()));

        String yaml = "key-3: value1";

        Node root = parser.parse(yaml);

        SingleSchemaValidationError e = assertThrows(SingleSchemaValidationError.class,
                () -> objectChecker.check(root));

        assertEquals("(1:1) Expecting one of: key1, key2 by available keys are: key-3", e.getMessage());

    }



    static class Bean {

        private String key;

        private Location keyLocation;

        private String value;

        private Location valueLocation;


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Location getKeyLocation() {
            return keyLocation;
        }

        public void setKeyLocation(Location keyLocation) {
            this.keyLocation = keyLocation;
        }

        public Location getValueLocation() {
            return valueLocation;
        }

        public void setValueLocation(Location valueLocation) {
            this.valueLocation = valueLocation;
        }
    }

}