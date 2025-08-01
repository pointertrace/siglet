package io.github.pointertrace.siglet.container.eventloop.processor;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.eventloop.MapSignalDestination;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorEventloopTest {

    @Test
    void process() {

        ProcessorFactory<MultiplyConfig> processorFactory = MultiplyBaseEventloopProcessor::new;

        ProcessorContext<MultiplyConfig> processorContext = new ProcessorContextImpl<>(new MultiplyConfig(2, "final"));

        ProcessorEventloop<MultiplyConfig> eventLoop = new ProcessorEventloop<>("event-loop", processorFactory,
                processorContext, SignalType.SIGNAL, 3, 5);

        assertEquals(State.CREATED, eventLoop.getState());

        MapSignalDestination finalDestination =
                new MapSignalDestination("final");

        eventLoop.connect(finalDestination);

        assertTimeout(Duration.ofSeconds(1), () -> {

            eventLoop.start();

            assertEquals(State.RUNNING, eventLoop.getState());

            eventLoop.send(new ValueSignal(1, 1));
            eventLoop.send(new ValueSignal(2, 2));
            eventLoop.send(new ValueSignal(3, 3));

            eventLoop.stop();

        });

        assertEquals(State.STOPPED, eventLoop.getState());

        assertTrue(finalDestination.has("1"));
        // 1 x 2 = 2
        assertEquals(2, finalDestination.get("1", ValueSignal.class).value);

        assertTrue(finalDestination.has("2"));
        // 2 x 2 = 4
        assertEquals(4, finalDestination.get("2", ValueSignal.class).value);

        assertTrue(finalDestination.has("3"));
        // 3 x 2 = 6
        assertEquals(6, finalDestination.get("3", ValueSignal.class).value);
    }

    @Test
    void process_twoEventLoops() {

        ProcessorFactory<MultiplyConfig> processorFactory = MultiplyBaseEventloopProcessor::new;

        ProcessorContext<MultiplyConfig> processorContextFirst = new ProcessorContextImpl<>(new MultiplyConfig(2, "second"));

        ProcessorEventloop<MultiplyConfig> first = new ProcessorEventloop<>("first", processorFactory,
                processorContextFirst, SignalType.SIGNAL, 3, 5);

        ProcessorContext<MultiplyConfig> processorContextSecond = new ProcessorContextImpl<>(new MultiplyConfig(5, "final"));

        ProcessorEventloop<MultiplyConfig> second = new ProcessorEventloop<>("second", processorFactory,
                processorContextSecond, SignalType.SIGNAL, 3, 5);

        MapSignalDestination finalDestination = new MapSignalDestination("final");

        first.connect(second);

        second.connect(finalDestination);

        assertTimeout(Duration.ofSeconds(1), () -> {

            first.start();
            second.start();

            first.send(new ValueSignal(1, 1));
            first.send(new ValueSignal(2, 2));
            first.send(new ValueSignal(3, 3));

            first.stop();
            second.stop();

        });

        assertTrue(finalDestination.has("1"));

        // 1 x 2 x 5 = 10
        assertEquals(10, finalDestination.get("1", ValueSignal.class).value);

        assertTrue(finalDestination.has("2"));

        // 2 x 2 x 5 = 20
        assertEquals(20, finalDestination.get("2", ValueSignal.class).value);

        assertTrue(finalDestination.has("3"));

        // 3 x 2 x 5 = 30
        assertEquals(30, finalDestination.get("3", ValueSignal.class).value);
    }


    @Test
    void process_highVolume() {

        ProcessorFactory<MultiplyConfig> processorFactory = MultiplyBaseEventloopProcessor::new;

        ProcessorContext<MultiplyConfig> processorContext = new ProcessorContextImpl<>(new MultiplyConfig(10, "final"));

        ProcessorEventloop<MultiplyConfig> eventLoop = new ProcessorEventloop<>("test", processorFactory,
                processorContext, SignalType.SIGNAL, 100_000, 5);

        assertEquals(State.CREATED, eventLoop.getState());

        MapSignalDestination finalDestination =
                new MapSignalDestination("final");

        eventLoop.connect(finalDestination);

        assertTimeout(Duration.ofSeconds(60), () -> {

            eventLoop.start();

            assertEquals(State.RUNNING, eventLoop.getState());

            for (int i = 0; i < 100_000; i++) {
                eventLoop.send(new ValueSignal(i, i));
            }

            eventLoop.stop();

        });


        assertEquals(State.STOPPED, eventLoop.getState());
        for (int i = 0; i < 100_000; i++) {
            assertEquals(i * 10, finalDestination.get("" + i, ValueSignal.class).value);
        }

    }

    @Test
    void process_processException() {

        ProcessorContext<Void> processorContext = new ProcessorContextImpl<>(null);

        ProcessorFactory<Void> processorFactory = ThrowExceptionBaseEventloopProcessor::new;

        ProcessorEventloop<Void> eventLoop = new ProcessorEventloop<Void>("test", processorFactory,
                processorContext, SignalType.SIGNAL, 10, 5);

        eventLoop.start();

        eventLoop.send(new ValueSignal(1, 1));

        eventLoop.stop();

    }


    private static class ValueSignal implements Signal {

        public final int id;

        public int value;

        ValueSignal(int id, int value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public String getId() {
            return "" + id;
        }
    }

    private static class ThrowExceptionBaseEventloopProcessor extends BaseEventloopProcessor<Void> {

        public ThrowExceptionBaseEventloopProcessor(ProcessorContext<Void> context) {
            super(context, ResultFactoryImpl.INSTANCE);
        }

        @Override
        protected Result process(Signal signal, ProcessorContext<Void> context, ResultFactory resultFactory) {
            throw new RuntimeException("Exception in processor");
        }
    }

    private static class MultiplyConfig {
        public final int factor;
        public final String next;

        public MultiplyConfig(int factor, String next) {
            this.factor = factor;
            this.next = next;
        }

    }

    private static class MultiplyBaseEventloopProcessor extends BaseEventloopProcessor<MultiplyConfig> {

        public MultiplyBaseEventloopProcessor(ProcessorContext<MultiplyConfig> context) {
            super(context, ResultFactoryImpl.INSTANCE);
        }

        @Override
        protected Result process(Signal signal, ProcessorContext<MultiplyConfig> context, ResultFactory resultFactory) {
            ValueSignal valueSignal = (ValueSignal) signal;

            valueSignal.value = valueSignal.value * context.getConfig().factor;


            return resultFactory.proceed();
        }
    }

}