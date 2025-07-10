package com.siglet.container.config.raw;

import com.siglet.SigletError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventLoopConfigTest {

    EventLoopConfig eventLoopConfig;

    @Test
    void chain() {

        EventLoopConfig chained = EventLoopConfig
                .of(1, 2)
                .chain(EventLoopConfig.of(3, 4))
                .chain(EventLoopConfig.of(5, 6));

        assertEquals(5, chained.getQueueSize());
        assertEquals(6, chained.getThreadPoolSize());

    }

    @Test
    void chain_firstEmpty() {

        EventLoopConfig chained = EventLoopConfig
                .empty()
                .chain(EventLoopConfig.of(1, 2));

        assertEquals(1, chained.getQueueSize());
        assertEquals(2, chained.getThreadPoolSize());

    }


    @Test
    void chain_nextEmpty() {

        EventLoopConfig chained = EventLoopConfig
                .of(1, 2)
                .chain(EventLoopConfig.empty());

        assertEquals(1, chained.getQueueSize());
        assertEquals(2, chained.getThreadPoolSize());

    }

    @Test
    void chain_allEmpty() {

        EventLoopConfig chained = EventLoopConfig
                .empty()
                .chain(EventLoopConfig.empty());

        assertThrowsExactly(SigletError.class, chained::getQueueSize);
        assertThrowsExactly(SigletError.class, chained::getThreadPoolSize);

    }

    @Test
    void of_objectNull() {

        eventLoopConfig = EventLoopConfig.of(null);

        assertInstanceOf(EventLoopConfig.EmptyEventLoopConfig.class, eventLoopConfig);

    }

    @Test
    void of_objectWithoutEventLoop() {

        eventLoopConfig = EventLoopConfig.of(new Object());

        assertInstanceOf(EventLoopConfig.EmptyEventLoopConfig.class, eventLoopConfig);

    }

    @Test
    void of_object() {

        eventLoopConfig = EventLoopConfig.of(new EventLoopConfig() {

            @Override
            public Integer getQueueSize() {
                return 1;
            }

            @Override
            public Integer getThreadPoolSize() {
                return 2;
            }
        });

        assertEquals(1, eventLoopConfig.getQueueSize());
        assertEquals(2, eventLoopConfig.getThreadPoolSize());
    }

}