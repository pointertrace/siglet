package com.siglet.container.config.raw;

import com.siglet.SigletError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueSizeConfigTest {

    QueueSizeConfig queueSizeConfig;

    @Test
    void chain() {

        QueueSizeConfig chained = QueueSizeConfig
                .of(1)
                .chain(QueueSizeConfig.of(2))
                .chain(QueueSizeConfig.of(3));

        assertEquals(3, chained.getQueueSize());

    }

    @Test
    void chain_firstEmpty() {

        QueueSizeConfig chained = QueueSizeConfig
                .empty()
                .chain(QueueSizeConfig.of(1));

        assertEquals(1, chained.getQueueSize());

    }


    @Test
    void chain_nextEmpty() {

        QueueSizeConfig chained = QueueSizeConfig
                .of(1)
                .chain(QueueSizeConfig.empty());

        assertEquals(1, chained.getQueueSize());

    }

    @Test
    void chain_allEmpty() {

        QueueSizeConfig chained = QueueSizeConfig
                .empty()
                .chain(QueueSizeConfig.empty());

        assertThrowsExactly(SigletError.class, chained::getQueueSize);

    }
}