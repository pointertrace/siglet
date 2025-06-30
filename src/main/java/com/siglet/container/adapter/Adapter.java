package com.siglet.container.adapter;

import com.google.protobuf.Message;
import com.siglet.SigletError;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Adapter<M extends Message, B extends Message.Builder> {

    private M message;

    private B builder;

    private Function<M, B> messageToBuilder;

    private Function<B, M> builderToMessage;

    private boolean updated;

    private boolean ready = false;

    public Adapter() {
    }

    // TODO remover construtor
    public Adapter (M message, Function<M, B> messageToBuilder, Function<B, M> builderToMessage) {
        this.message = message;
        this.messageToBuilder = messageToBuilder;
        this.builderToMessage = builderToMessage;
        this.ready = true;
    }

    // TODO remover construtor
    public Adapter(B builder, Function<B, M> builderToMessage) {
        this.builder = builder;
        this.builderToMessage = builderToMessage;
        this.updated = true;
        this.ready = true;
    }

    protected void recycle(M message, Function<M, B> messageToBuilder, Function<B, M> builderToMessage) {
        this.message = message;
        this.messageToBuilder = messageToBuilder;
        this.builderToMessage = builderToMessage;
        this.ready = true;
    }

    public void recycle(B builder, Function<B, M> builderToMessage) {
        this.builder = builder;
        this.builderToMessage = builderToMessage;
        this.updated = true;
        this.ready = true;
    }

    public void clear() {
        this.message = null;
        this.builder = null;
        this.messageToBuilder = null;
        this.builderToMessage = null;
        this.updated = false;
        this.ready = false;
    }

    protected <T> T getValue(Function<M, T> messageGetter, Function<B, T> builderGetter) {
        checkReady();
        if (builder != null) {
            return builderGetter.apply(builder);
        } else {
            return messageGetter.apply(message);
        }
    }

    protected <T> void setValue(BiConsumer<B, T> setter, T value) {
        prepareUpdate();
        setter.accept(builder, value);
    }

    public M getUpdated() {
        checkReady();
        if (!isUpdated()) {
            return message;
        } else {
            if (builder == null) {
                builder = messageToBuilder.apply(message);
            }
            enrich(builder);
            return builderToMessage.apply(builder);
        }
    }

    public <T> boolean test(Function<M, T> messageGetter, Function<B, T> builderGetter, Predicate<T> predicate) {
        checkReady();
        if (builder != null) {
            return predicate.test(builderGetter.apply(builder));
        } else {
            return predicate.test(messageGetter.apply(message));
        }
    }

    protected M getMessage() {
        checkReady();
        return message;
    }

    protected B getBuilder() {
        checkReady();
        return builder;
    }

    protected void enrich(B builder) {
    }

    public boolean isUpdated() {
        checkReady();
        return updated;
    }

    public boolean isReady() {
        return ready;
    }

    protected void prepareUpdate() {
        checkReady();
        if (builder == null) {
            builder = messageToBuilder.apply(message);
        }
        if (!updated) {
            updated = true;
        }
    }

    private void checkReady() {
        if (!ready) {
            throw new SigletError("Cannot use a not ready adapter");
        }
    }

}
