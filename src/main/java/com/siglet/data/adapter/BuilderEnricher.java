package com.siglet.data.adapter;

import com.google.protobuf.Message;


@FunctionalInterface
public interface BuilderEnricher<T extends Message.Builder> {

    void enrich(T builder);

}
