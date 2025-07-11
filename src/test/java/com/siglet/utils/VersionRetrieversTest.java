package com.siglet.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VersionRetrieversTest {


    @Test
    void get(){
        assertNotNull(VersionRetrievers.get());
        assertNotEquals("undetermined", VersionRetrievers.get());
    }

}