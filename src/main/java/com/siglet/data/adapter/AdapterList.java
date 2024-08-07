package com.siglet.data.adapter;

import com.google.protobuf.Message;
import com.siglet.SigletError;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AdapterList<M extends Message, B extends Message.Builder, A extends Adapter<M, B>> {

    private List<M> messages;

    private List<A> adapters;

    private final boolean updatable;

    private boolean updated;

    public AdapterList(List<M> messages, boolean updatable) {
        this.messages = messages;
        this.updatable = updatable;
    }

    public List<M> getUpdated() {
        if (!updatable) {
            return messages;
        } else if (!isUpdated()) {
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
        return messages.size();
    }

    public boolean isUpdated() {
        return updated || (adapters != null && adapters.stream().filter(Objects::nonNull).anyMatch(Adapter::isUpdated));
    }

    public boolean isUpdatable() {
        return updatable;
    }

    protected A add() {
        checkAndPrepareUpdate();
        A newAdapter = createNewAdapter();
        adapters.add(newAdapter);
        messages.add(null);
        return newAdapter;
    }

    protected void remove(int i) {
        checkAndPrepareUpdate();
        adapters.remove(i);
        messages.remove(i);
    }

    protected boolean remove(Predicate<M> messagePredicate, Predicate<B> builderPredicate) {
        checkAndPrepareUpdate();
        int i = findIndex(messagePredicate, builderPredicate);
        if (i >= 0) {
            remove(i);
            return true;
        } else {
            return false;
        }
    }


    protected A find(Predicate<M> messagePredicate, Predicate<B> builderPredicate) {
        int idx = findIndex(messagePredicate, builderPredicate);
        if (idx >= 0) {
            return getAdapter(idx);
        } else {
            return null;
        }
    }


    protected int findIndex(Predicate<M> messagePredicate, Predicate<B> builderPredicate) {
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

    protected A getAdapter(int i) {
        initAdaptersIfNeeded();
        A adapter = adapters.get(i);
        if (adapter == null) {
            adapter = createAdapter(i);
            adapters.set(i, adapter);
        }
        return adapter;
    }

    protected boolean hasAdapter(int i) {
        return (adapters != null && adapters.get(i) != null);
    }

    protected M getMessage(int i) {
        return messages.get(i);
    }

    protected abstract A createNewAdapter();

    protected abstract A createAdapter(int i);

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("list is not updatable!");
        } else {
            messages = new ArrayList<>(messages);
            initAdaptersIfNeeded();
            updated = true;
        }
    }

    private void initAdaptersIfNeeded() {
        if (adapters == null) {
            adapters = new ArrayList<>(messages.size());
            for (int i = 0; i < messages.size(); i++) {
                adapters.add(null);
            }
        }
    }

}
