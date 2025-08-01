package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.container.SigletError;

public interface QueueSizeConfig {

    Integer getQueueSize();

    static QueueSizeConfig of(int queueSize) {
        return new SimpleEventLoopConfig(queueSize);
    }

    static QueueSizeConfig of(Object queueSize) {
        if (queueSize != null && queueSize instanceof QueueSizeConfig queueSizeConfig) {
            return queueSizeConfig;
        } else {
            return QueueSizeConfig.empty();
        }
    }

    static QueueSizeConfig empty() {
        return new EmptyEventLoopConfig();
    }

    static QueueSizeConfig defaultConfig() {
        return new SimpleEventLoopConfig(1_000);
    }

    default QueueSizeConfig chain(QueueSizeConfig next) {
        return new ChainedEventLoopConfig(this, next);
    }


    class EmptyEventLoopConfig implements QueueSizeConfig {

        @Override
        public Integer getQueueSize() {
            return null;
        }

    }

    class SimpleEventLoopConfig implements QueueSizeConfig {

        private final Integer queueSize;

        public SimpleEventLoopConfig(Integer queueSize) {
            this.queueSize = queueSize;
        }

        @Override
        public Integer getQueueSize() {
            return queueSize;
        }

    }

    class ChainedEventLoopConfig implements QueueSizeConfig {

        private final QueueSizeConfig first;

        private final QueueSizeConfig next;

        public ChainedEventLoopConfig(QueueSizeConfig first, QueueSizeConfig next) {
            this.first = first;
            this.next = next;
        }

        @Override
        public Integer getQueueSize() {
            Integer queueSize = first == null ? null: first.getQueueSize();
            if (next != null && next.getQueueSize() != null) {
                queueSize = next.getQueueSize();
            }
            if (queueSize == null) {
                throw new SigletError("Could not determine queue size for event loop");
            }
            return queueSize;
        }

    }

}
