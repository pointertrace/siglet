package com.siglet.container.config.raw;

import com.siglet.SigletError;

public interface EventLoopConfig {

    Integer getQueueSize();

    Integer getThreadPoolSize();

    static EventLoopConfig of(int queueSize, int threadPoolSize) {
        return new SimpleEventLoopConfig(queueSize, threadPoolSize);
    }

    static EventLoopConfig empty() {
        return new EmptyEventLoopConfig();
    }

    static EventLoopConfig of(Object eventLoopObject) {
        if (eventLoopObject != null && eventLoopObject instanceof EventLoopConfig eventLoopConfig) {
            return new SimpleEventLoopConfig(eventLoopConfig.getQueueSize(), eventLoopConfig.getThreadPoolSize());
        } else {
            return new EmptyEventLoopConfig();
        }
    }

    default EventLoopConfig chain(EventLoopConfig next) {
        return new ChainedEventLoopConfig(this, next);
    }


    class EmptyEventLoopConfig implements EventLoopConfig {

        @Override
        public Integer getQueueSize() {
            return null;
        }

        @Override
        public Integer getThreadPoolSize() {
            return null;
        }
    }

    class SimpleEventLoopConfig implements EventLoopConfig {

        private final Integer queueSize;

        private final Integer threadPoolSize;

        public SimpleEventLoopConfig(Integer queueSize, Integer threadPoolSize) {
            this.queueSize = queueSize;
            this.threadPoolSize = threadPoolSize;
        }

        @Override
        public Integer getQueueSize() {
            return queueSize;
        }

        @Override
        public Integer getThreadPoolSize() {
            return threadPoolSize;
        }
    }

    class ChainedEventLoopConfig implements EventLoopConfig {

        private final EventLoopConfig first;

        private final EventLoopConfig next;

        public ChainedEventLoopConfig(EventLoopConfig first, EventLoopConfig next) {
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

        @Override
        public Integer getThreadPoolSize() {
            Integer threadPoolSize = first == null ? null: first.getThreadPoolSize();
            if (next != null && next.getThreadPoolSize() != null) {
                threadPoolSize = next.getThreadPoolSize();
            }
            if (threadPoolSize == null) {
                throw new SigletError("Could not determine thread pool size for event loop");
            }
            return threadPoolSize;
        }
    }

}
