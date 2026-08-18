package io.github.pointertrace.siglet.impl.eventloop;

public interface ReceiveFunction<IN> {

    boolean receive(IN in);

}
