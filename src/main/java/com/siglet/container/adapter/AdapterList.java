package com.siglet.container.adapter;

import com.google.protobuf.Message;
import com.siglet.SigletError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class AdapterList<M extends Message, B extends Message.Builder, A extends Adapter<M, B>> {

    private List<M> messages;

    private List<A> adapters;

    private boolean updated;

    private boolean ready;

    private final AdapterListConfig<M,B,A,?> config;

    protected AdapterList(AdapterListConfig<M,B,A,?> config) {
        this.config = config;
    }

    public void recycle(List<M> messages) {
        this.messages = messages;
        ready = true;
    }

    public void clear() {
        messages = null;
        adapters = null;
        updated = false;
        ready = false;
    }

    public List<M> getUpdated() {
        checkReady();
        if (!isUpdated()) {
            return messages;
        } else {
            List<M> updatedList = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                if (hasAdapter(i)) {
                    updatedList.add(adapters.get(i).getUpdated());
                } else {
                    updatedList.add(messages.get(i));
                }
            }
            return updatedList;
        }
    }

    public int getSize() {
        checkReady();
        return messages.size();
    }

    public boolean isUpdated() {
        checkReady();
        return updated || (adapters != null && adapters.stream().filter(Objects::nonNull).anyMatch(Adapter::isUpdated));
    }

    public boolean isReady() {
        return ready;
    }

    public A add() {
        prepareUpdate();
        A newAdapter =  createNewAdapter();
        adapters.add(newAdapter);
        messages.add(null);
        return newAdapter;
    }

    public void remove(int i) {
        prepareUpdate();
        adapters.remove(i);
        messages.remove(i);
    }

    protected boolean remove(Predicate<M> messagePredicate, Predicate<B> builderPredicate) {
        prepareUpdate();
        int i = findIndex(messagePredicate, builderPredicate);
        if (i >= 0) {
            remove(i);
            return true;
        } else {
            return false;
        }
    }


    protected A find(Predicate<M> messagePredicate, Predicate<B> builderPredicate) {
        checkReady();
        int idx = findIndex(messagePredicate, builderPredicate);
        if (idx >= 0) {
            return get(idx);
        } else {
            return null;
        }
    }


    protected int findIndex(Predicate<M> messagePredicate, Predicate<B> builderPredicate) {
        checkReady();
        for (int i = 0; i < messages.size(); i++) {
            if (hasAdapter(i)) {
                A adapter = adapters.get(i);
                if (adapter.getMessage() != null) {
                    if (messagePredicate.test(adapter.getMessage())) {
                        return i;
                    }
                } else {
                    if (builderPredicate.test(adapter.getBuilder())) {
                        return i;
                    }
                }
            } else {
                M message = getMessage(i);
                if (messagePredicate.test(message)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public A get(int i) {
        checkReady();
        initAdaptersIfNeeded();
        A adapter = adapters.get(i);
        if (adapter == null) {
            adapter = createAdapter(i);
            adapters.set(i, adapter);
        }
        return adapter;
    }

    protected boolean hasAdapter(int i) {
        checkReady();
        return (adapters != null && adapters.get(i) != null);
    }

    protected M getMessage(int i) {
        checkReady();
        return messages.get(i);
    }

    protected A createNewAdapter() {
        A adapter = config.adapterCreator().get();
        adapter.recycle(config.builderCreator().get());
        return adapter;
    }

    protected A createAdapter(int i) {
        A adapter = config.adapterCreator().get();
        adapter.recycle(getMessage(i));
        return adapter;
    }

    private void prepareUpdate() {
        checkReady();
        messages = new ArrayList<>(messages);
        initAdaptersIfNeeded();
        updated = true;
    }

    private void initAdaptersIfNeeded() {
        if (adapters == null) {
            adapters = new ArrayList<>(messages.size());
            for (int i = 0; i < messages.size(); i++) {
                adapters.add(null);
            }
        }
    }

    private void checkReady() {
        if (!ready) {
            throw new SigletError("Cannot use a not ready adapter");
        }
    }

}
