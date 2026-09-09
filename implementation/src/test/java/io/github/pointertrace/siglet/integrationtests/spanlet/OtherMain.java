package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;
import io.github.pointertrace.siglet.impl.engine.metric.Metrics;
import io.github.pointertrace.siglet.impl.engine.metric.otelgrpc.OtelGrpcMetrics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

public class OtherMain {


    private static final int THREADS = 1000;
    private static final int EXECUTIONS_PER_THREAD = 100;

    static LongAdder droppedSignalsCounterAdder;

    static long inicio = 0;

    public static void main(String[] args) throws InterruptedException {

        Metrics metrics = new OtelGrpcMetrics(1000, null);

        droppedSignalsCounterAdder = new LongAdder();

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch finishSignal = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            final int threadId = i;

            Thread thread = new Thread(() -> {
                try {
                    // Aguarda o sinal de início
                    if (inicio == 0) {
                        inicio = System.currentTimeMillis();
                    }
                    startSignal.await();

                    // Executa 100 vezes
                    for (int j = 0; j < EXECUTIONS_PER_THREAD; j++) {
                        doWork(threadId, j);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishSignal.countDown();
                }
            });

            thread.start();
        }

        System.out.println("Todas as threads criadas.");

        // Libera todas as threads aproximadamente ao mesmo tempo
        startSignal.countDown();

        // Aguarda todas terminarem
        finishSignal.await();

        System.out.println("Todas as threads finalizaram.");
        System.out.println("Tempo total=" + (System.currentTimeMillis() - inicio) + "ms");
        System.out.println("adder" +droppedSignalsCounterAdder.intValue());
    }

    private static void doWork(int threadId, int execution) {
        droppedSignalsCounterAdder.increment();
    }
}
