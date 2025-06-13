package com.siglet.container.adapter;

import com.google.protobuf.Message;


@FunctionalInterface
public interface BuilderEnricher<T extends Message.Builder> {

    void enrich(T builder);

}
