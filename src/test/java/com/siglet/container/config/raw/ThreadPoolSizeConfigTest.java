package com.siglet.container.config.raw;

import com.siglet.SigletError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ThreadPoolSizeConfigTest {

    ThreadPoolSizeConfig threadPoolSizeConfig;

    @Test
    void chain() {

        ThreadPoolSizeConfig chained = ThreadPoolSizeConfig
                .of(1)
                .chain(ThreadPoolSizeConfig.of(2))
                .chain(ThreadPoolSizeConfig.of(3));

        assertEquals(3, chained.getThreadPoolSize());

    }

    @Test
    void chain_firstEmpty() {

        ThreadPoolSizeConfig chained = ThreadPoolSizeConfig
                .empty()
                .chain(ThreadPoolSizeConfig.of(1));

        assertEquals(1, chained.getThreadPoolSize());

    }


    @Test
    void chain_nextEmpty() {

        ThreadPoolSizeConfig chained = ThreadPoolSizeConfig
                .of(1)
                .chain(ThreadPoolSizeConfig.empty());

        assertEquals(1, chained.getThreadPoolSize());

    }

    @Test
    void chain_allEmpty() {

        ThreadPoolSizeConfig chained = ThreadPoolSizeConfig
                .empty()
                .chain(ThreadPoolSizeConfig.empty());

        assertThrowsExactly(SigletError.class, chained::getThreadPoolSize);

    }
}