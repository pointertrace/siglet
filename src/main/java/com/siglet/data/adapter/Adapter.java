package com.siglet.data.adapter;

import com.google.protobuf.Message;
import com.siglet.SigletError;
import com.siglet.data.CloneableAdapter;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Adapter<M extends Message, B extends Message.Builder> {

    private M message;

    private B builder;

    private Function<M, B> messageToBuilder;

    private final Function<B, M> builderToMessage;

    private final boolean updatable;

    private boolean updated;

    public Adapter(M message, Function<M, B> messageToBuilder, Function<B, M> builderToMessage, boolean updatable) {
        this.message = message;
        this.messageToBuilder = messageToBuilder;
        this.builderToMessage = builderToMessage;
        this.updatable = updatable;
    }

    public Adapter(B builder, Function<B, M> builderToMessage) {
        this.builder = builder;
        this.builderToMessage = builderToMessage;
        this.updatable = true;
        this.updated = true;
    }

    protected <T> T getValue(Function<M, T> messageGetter, Function<B, T> builderGetter) {
        if (builder != null) {
            return builderGetter.apply(builder);
        } else {
            return messageGetter.apply(message);
        }
    }

    protected <T> void setValue(BiConsumer<B, T> setter, T value) {
        checkAndPrepareUpdate();
        setter.accept(builder, value);
    }

    public M getUpdated() {
        if (!updatable) {
            return message;
        } else if (!isUpdated()) {
            return message;
        } else {
            if (builder == null) {
                builder = messageToBuilder.apply(message);
            }
            enrich(builder);
            return builderToMessage.apply(builder);
        }
    }

    public <T> boolean test(Function<M,T> messageGetter, Function<B,T> builderGetter, Predicate<T> predicate) {
        if (builder != null) {
            return predicate.test(builderGetter.apply(builder));
        } else {
            return predicate.test(messageGetter.apply(message));
        }
    }

    protected M getMessage() {
        return message;
    }

    protected B getBuilder() {
        return builder;
    }

    protected void enrich(B builder) {
    }

    public boolean isUpdated() {
        return updated;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    protected void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable adapter!");
        }
        if (builder == null) {
            builder = messageToBuilder.apply(message);
        }
        if (!updated) {
            updated = true;
        }
    }

}
