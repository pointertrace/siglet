package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.grpc.netty.shaded.io.netty.channel.EventLoopGroup;
import io.grpc.netty.shaded.io.netty.util.concurrent.EventExecutor;
import io.grpc.netty.shaded.io.netty.util.concurrent.SingleThreadEventExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyEventLoopMetrics {

    private NettyEventLoopMetrics() {
    }

    public static void register(
            EventLoopGroup workerGroup) {

        AtomicInteger totalPendingTasks =
                new AtomicInteger();

        AtomicInteger aliveLoops =
                new AtomicInteger();
//
//        Gauge.builder("siglet.netty.eventloop.pending.tasks.total", totalPendingTasks, AtomicInteger::get)
//                .description("Total pending tasks across all event loops")
//                .register(registry);
//
//        Gauge.builder(
//                        "netty.eventloop.alive", aliveLoops, AtomicInteger::get).description("Active event loops")
//                .register(registry);
//
//        AtomicInteger loopCount = new AtomicInteger(0);
//
//        for (EventExecutor executor : workerGroup) {
//
//            loopCount.incrementAndGet();
//
//            if (executor instanceof SingleThreadEventExecutor stee) {
//
//                Gauge.builder("siglet.netty.eventloop.pending.tasks", stee, SingleThreadEventExecutor::pendingTasks)
//                        .tag("eventloop", Integer.toHexString(System.identityHashCode(stee)))
//                        .register(registry);
//            }
//        }
//
//        Gauge.builder("siglet.netty.eventloop.count", loopCount::get).register(registry);
//
//
        ScheduledExecutorService monitor =
                Executors.newSingleThreadScheduledExecutor(
                        r -> {
                            Thread t = new Thread(r, "netty-metrics-monitor");
                            t.setDaemon(true);
                            return t;
                        });

        monitor.scheduleAtFixedRate(
                () -> {

                    int pending = 0;
                    int alive = 0;

                    for (EventExecutor executor : workerGroup) {

                        if (executor instanceof SingleThreadEventExecutor stee) {

                            pending += stee.pendingTasks();

                            if (!stee.isShuttingDown()) {
                                alive++;
                            }
                        }
                    }

                    totalPendingTasks.set(pending);
                    aliveLoops.set(alive);

                },
                0,
                5,
                TimeUnit.SECONDS);
    }
}
