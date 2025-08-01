package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siglet.container.config.siglet.parser.ClassValueTransformer;
import io.github.pointertrace.siget.parser.ValueTransformerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ClassValueTransformerTest {


    private ClassValueTransformer classValueTransformer;


    @Test
    void transform() throws Exception {

        classValueTransformer = new ClassValueTransformer();

        assertEquals(String.class, classValueTransformer.transform(String.class.getName()));

    }

    @Test
    void transform_classNotFound() {

        classValueTransformer = new ClassValueTransformer();

        ValueTransformerException ex = assertThrowsExactly(ValueTransformerException.class,
                () -> classValueTransformer.transform("non existing class"));


        assertEquals("Class non existing class not found", ex.getMessage());

    }

    @Test
    void transform_valueIsNotString() {

        classValueTransformer = new ClassValueTransformer();

        ValueTransformerException ex = assertThrowsExactly(ValueTransformerException.class,
                () -> classValueTransformer.transform(10));


        assertEquals("The value is java.lang.Integer and should be a String", ex.getMessage());

    }

    @Test
    void transform_valueIsNull() {

        classValueTransformer = new ClassValueTransformer();

        ValueTransformerException ex = assertThrowsExactly(ValueTransformerException.class,
                () -> classValueTransformer.transform(null));


        assertEquals("The value is null", ex.getMessage());

    }

    @Test
    void transform_valueIsNotAssignable() {

        classValueTransformer = new ClassValueTransformer(String.class);

        ValueTransformerException ex = assertThrowsExactly(ValueTransformerException.class,
                () -> classValueTransformer.transform(Object.class.getName()));


        assertEquals("Class java.lang.Object is not assignable from java.lang.String.", ex.getMessage());

    }

    @Test
    void transform_valueIsAssignable() throws Exception {

        classValueTransformer = new ClassValueTransformer(Number.class);

        assertEquals(Integer.class,classValueTransformer.transform(Integer.class.getName()));

    }
}