package com.siglet.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {


    @Test
    void get(){
        assertNotNull(Version.get());
        assertNotEquals("undetermined", Version.get());
    }

}