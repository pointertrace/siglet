package io.github.pointertrace.siglet.impl.eventloop.processor;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class EventloopTest {

    @Test
    void process() {

        ProcessorFactory<MultiplyConfig> processorFactory = MultiplyBaseProcessor::new;

        Context<MultiplyConfig> context = new ContextImpl<>(new MultiplyConfig(2, "final"));

        Eventloop<MultiplyConfig> eventLoop = new Eventloop<>("event-loop", processorFactory,
                context, SignalCapabilities.of(ValueSignal.class), SignalCapabilities.of(ValueSignal.class), 3, 5);

        assertEquals(State.CREATED, eventLoop.getState());

        MockSignalDestination destination = new MockSignalDestination("destination", SignalCapabilities.of(ValueSignal.class));

        eventLoop.connect(destination);

        assertTimeout(Duration.ofSeconds(1), () -> {

            eventLoop.start();

            assertEquals(State.RUNNING, eventLoop.getState());

            eventLoop.send(new ValueSignal(1, 1));
            eventLoop.send(new ValueSignal(2, 2));
            eventLoop.send(new ValueSignal(3, 3));

            eventLoop.stop();

        });

        assertEquals(State.STOPPED, eventLoop.getState());

        assertTrue(destination.has("1"));
        // 1 x 2 = 2
        assertEquals(2, destination.get("1", ValueSignal.class).value);

        assertTrue(destination.has("2"));
        // 2 x 2 = 4
        assertEquals(4, destination.get("2", ValueSignal.class).value);

        assertTrue(destination.has("3"));
        // 3 x 2 = 6
        assertEquals(6, destination.get("3", ValueSignal.class).value);
    }

    @Test
    void process_twoEventLoops() {

        ProcessorFactory<MultiplyConfig> processorFactory = MultiplyBaseProcessor::new;

        Context<MultiplyConfig> contextFirst = new ContextImpl<>(new MultiplyConfig(2, "second"));

        Eventloop<MultiplyConfig> firstEventloop = new Eventloop<>("first", processorFactory,
                contextFirst,SignalCapabilities.of(ValueSignal.class),SignalCapabilities.of(ValueSignal.class), 3, 5);

        Context<MultiplyConfig> contextSecond = new ContextImpl<>(new MultiplyConfig(5, "final"));

        Eventloop<MultiplyConfig> secondEventLoop = new Eventloop<>("second", processorFactory,
                contextSecond, SignalCapabilities.of(ValueSignal.class),SignalCapabilities.of(ValueSignal.class), 3, 5);

        MockSignalDestination destination = new MockSignalDestination("destination",SignalCapabilities.of(ValueSignal.class));

        firstEventloop.connect(secondEventLoop);

        secondEventLoop.connect(destination);

        assertTimeout(Duration.ofSeconds(1), () -> {

            firstEventloop.start();
            secondEventLoop.start();

            firstEventloop.send(new ValueSignal(1, 1));
            firstEventloop.send(new ValueSignal(2, 2));
            firstEventloop.send(new ValueSignal(3, 3));

            firstEventloop.stop();
            secondEventLoop.stop();

        });

        assertTrue(destination.has("1"));

        // 1 x 2 x 5 = 10
        assertEquals(10, destination.get("1", ValueSignal.class).value);

        assertTrue(destination.has("2"));

        // 2 x 2 x 5 = 20
        assertEquals(20, destination.get("2", ValueSignal.class).value);

        assertTrue(destination.has("3"));

        // 3 x 2 x 5 = 30
        assertEquals(30, destination.get("3", ValueSignal.class).value);
    }


    @Test
    void process_highVolume() {

        ProcessorFactory<MultiplyConfig> processorFactory = MultiplyBaseProcessor::new;

        Context<MultiplyConfig> context = new ContextImpl<>(new MultiplyConfig(10, "final"));

        Eventloop<MultiplyConfig> eventLoop = new Eventloop<>("test", processorFactory,
                context, SignalCapabilities.of(ValueSignal.class), SignalCapabilities.of(ValueSignal.class), 100_000, 5);

        assertEquals(State.CREATED, eventLoop.getState());

        MockSignalDestination destination =
                new MockSignalDestination("destination", SignalCapabilities.of(ValueSignal.class));

        eventLoop.connect(destination);

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
            assertEquals(i * 10, destination.get("" + i, ValueSignal.class).value);
        }

    }

    @Test
    void process_processException() {

        Context<Void> context = new ContextImpl<>(null);

        ProcessorFactory<Void> processorFactory = ThrowExceptionBaseProcessor::new;

        Eventloop<Void> eventLoop = new Eventloop<Void>("test", processorFactory,
                context,SignalCapabilities.of(ValueSignal.class) ,SignalCapabilities.of(ValueSignal.class), 10, 5);

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

    private static class ThrowExceptionBaseProcessor extends BaseProcessor<Void> {

        public ThrowExceptionBaseProcessor(Context<Void> context) {
            super(context, ResultFactoryImpl.INSTANCE);
        }

        @Override
        protected Result process(Signal signal, Context<Void> context, ResultFactory resultFactory) {
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

    private static class MultiplyBaseProcessor extends BaseProcessor<MultiplyConfig> {

        public MultiplyBaseProcessor(Context<MultiplyConfig> context) {
            super(context, ResultFactoryImpl.INSTANCE);
        }

        @Override
        protected Result process(Signal signal, Context<MultiplyConfig> context, ResultFactory resultFactory) {
            ValueSignal valueSignal = (ValueSignal) signal;

            valueSignal.value = valueSignal.value * context.getConfig().factor;


            return resultFactory.proceed();
        }
    }

}