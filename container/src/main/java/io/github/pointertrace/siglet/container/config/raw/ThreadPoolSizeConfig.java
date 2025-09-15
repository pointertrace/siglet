package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.api.SigletError;

public interface ThreadPoolSizeConfig {

    Integer getThreadPoolSize();

    static ThreadPoolSizeConfig of(int threadPoolSize) {
        return new SimpleThreadPoolSizeConfig(threadPoolSize);
    }

    static ThreadPoolSizeConfig empty() {
        return new EmptyThreadPoolSizeConfig();
    }

    static ThreadPoolSizeConfig defaultConfig() {
        return new SimpleThreadPoolSizeConfig(Runtime.getRuntime().availableProcessors());
    }

    static ThreadPoolSizeConfig of(Object eventLoopObject) {
        if (eventLoopObject != null && eventLoopObject instanceof ThreadPoolSizeConfig eventLoopConfig) {
            return new SimpleThreadPoolSizeConfig(eventLoopConfig.getThreadPoolSize());
        } else {
            return new EmptyThreadPoolSizeConfig();
        }
    }

    default ThreadPoolSizeConfig chain(ThreadPoolSizeConfig next) {
        return new ChainedEventLoopConfig(this, next);
    }


    class EmptyThreadPoolSizeConfig implements ThreadPoolSizeConfig {

        @Override
        public Integer getThreadPoolSize() {
            return null;
        }
    }

    class SimpleThreadPoolSizeConfig implements ThreadPoolSizeConfig {

        private final Integer threadPoolSize;

        public SimpleThreadPoolSizeConfig(Integer threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
        }

        @Override
        public Integer getThreadPoolSize() {
            return threadPoolSize;
        }
    }

    class ChainedEventLoopConfig implements ThreadPoolSizeConfig {

        private final ThreadPoolSizeConfig first;

        private final ThreadPoolSizeConfig next;

        public ChainedEventLoopConfig(ThreadPoolSizeConfig first, ThreadPoolSizeConfig next) {
            this.first = first;
            this.next = next;
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
