package io.github.pointertrace.siglet.impl.adapter;

import com.google.protobuf.Message;
import io.github.pointertrace.siglet.api.SigletError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

public class Adapter<M extends Message, B extends Message.Builder> {

    private M message;

    private B builder;

    private final AdapterConfig<M, B> config;

    private boolean updated;

    private boolean ready = false;

    private final Map<AdapterConfig<?, ?>, Adapter<?, ?>> childrenAdapter = new HashMap<>();

    private final Map<AdapterListConfig<?, ?, ?, ?>, AdapterList<?, ?, ?>> childrenListAdapter = new HashMap<>();

    private final Map<AdapterListConfig<?, ?, ?, ?>, Consumer<List<?>>> listEnrichers = new HashMap<>();

    private final Map<AdapterConfig<?, ?>, Consumer<Message>> enrichers = new HashMap<>();


    protected Adapter(AdapterConfig<M, B> config, AdapterConfig<?, ?>... childrenConfig) {
        this.config = config;
        for (AdapterConfig<?, ?> childConfig : childrenConfig) {
            childrenAdapter.put(childConfig, null);
        }
    }

    protected Adapter(AdapterConfig<M, B> config, List<AdapterConfig<?, ?>> childrenConfig,
                      List<AdapterListConfig<?, ?, ?,?>> childrenListConfig) {
        this.config = config;
        for (AdapterConfig<?, ?> childConfig : childrenConfig) {
            childrenAdapter.put(childConfig, null);
        }
        for (AdapterListConfig<?, ?, ?,?> childListConfig : childrenListConfig) {
            childrenListAdapter.put(childListConfig, null);
        }
    }

    protected void addEnricher(AdapterListConfig<?, ?, ?,?> adapterListConfig, Consumer<List<?>> enricherFunction) {
        listEnrichers.put(adapterListConfig, enricherFunction);
    }

    protected void addEnricher(AdapterConfig<?, ?> adapterConfig, Consumer<Message> enricherFunction) {
        enrichers.put(adapterConfig, enricherFunction);
    }

    protected <CM extends Message, CB extends Message.Builder, CA extends Adapter<CM, CB>,
            CAL extends AdapterList<CM, CB, CA>> CAL getAdapterList(
            AdapterListConfig<CM, CB, CA, CAL> config, Supplier<List<CM>> childMessageListGetter) {

        CAL  childList = (CAL) childrenListAdapter.computeIfAbsent(config,
                cfg -> {
            AdapterList<CM, CB, CA> cld = (AdapterList<CM, CB, CA>) cfg.adapterListCreator().get();
            cld.recycle(childMessageListGetter.get());
            return cld;
        });

        if (!childList.isReady()) {
            childList.recycle(childMessageListGetter.get());
        }

        return childList;
    }

    protected <CM extends Message, CB extends Message.Builder,CA extends Adapter<CM, CB>> CA getAdapter(
            AdapterConfig<CM, CB> config, Supplier<CM> childMessageGetter) {

        Adapter<CM, CB> child = (Adapter<CM, CB>) childrenAdapter.computeIfAbsent(config, cfg -> {
            Adapter<CM, CB> cd = (Adapter<CM, CB>) cfg.adapterCreator().get();
            cd.recycle(childMessageGetter.get());
            return cd;
        });

        if (!child.isReady()) {
            child.recycle(childMessageGetter.get());
        }

        return (CA) child;
    }

    protected boolean hasAdapter(AdapterConfig<?, ?> config) {
        return childrenAdapter.containsKey(config);
    }

    public void recycle(M message) {
        this.message = message;
        this.ready = true;
    }

    public void recycle(B builder) {
        this.builder = builder;
        this.updated = true;
        this.ready = true;
    }

    public void clear() {
        this.message = null;
        this.builder = null;
        this.updated = false;
        this.ready = false;
        for (Map.Entry<AdapterListConfig<?, ?, ?, ?>, Consumer<List<?>>> enricher : listEnrichers.entrySet()) {
            AdapterList<?, ?, ?> adapterList = childrenListAdapter.get(enricher.getKey());
            if (adapterList != null) {
                adapterList.clear();
            }
        }
        for (Map.Entry<AdapterConfig<?, ?>, Consumer<Message>> enricher : enrichers.entrySet()) {
            Adapter<?, ?> adapter = childrenAdapter.get(enricher.getKey());
            if (adapter != null) {
                adapter.clear();
            }
        }
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
                builder = config.messageToBuilder().apply(message);
            }
            enrich();
            return config.builderToMessage().apply(builder);
        }
    }

    public void clearChanges() {
        checkReady();
        if (builder != null) {
            builder.clear();
        }
        for (Map.Entry<AdapterListConfig<?, ?, ?, ?>, Consumer<List<?>>> enricher : listEnrichers.entrySet()) {
            AdapterList<?, ?, ?> adapterList = childrenListAdapter.get(enricher.getKey());
            if (adapterList != null && adapterList.isUpdated()) {
                adapterList.clearChanges();
            }
        }
        for (Map.Entry<AdapterConfig<?, ?>, Consumer<Message>> enricher : enrichers.entrySet()) {
            Adapter<?, ?> adapter = childrenAdapter.get(enricher.getKey());
            if (adapter != null && adapter.isUpdated()) {
                adapter.clearChanges();
            }
        }
        updated = false;
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

    protected void enrich() {
        for (Map.Entry<AdapterListConfig<?, ?, ?, ?>, Consumer<List<?>>> enricher : listEnrichers.entrySet()) {
            AdapterList<?, ?, ?> adapterList = childrenListAdapter.get(enricher.getKey());
            if (adapterList != null && adapterList.isUpdated()) {
                enricher.getValue().accept(adapterList.getUpdated());
            }
        }
        for (Map.Entry<AdapterConfig<?, ?>, Consumer<Message>> enricher : enrichers.entrySet()) {
            Adapter<?, ?> adapter = childrenAdapter.get(enricher.getKey());
            if (adapter != null && adapter.isUpdated()) {
                enricher.getValue().accept(adapter.getUpdated());
            }
        }
    }

    public boolean isUpdated() {
        checkReady();
        if (updated) {
            return true;
        }
        for (Adapter<?, ?> child : childrenAdapter.values()) {
            if (child != null && child.isUpdated()) {
                return true;
            }
        }
        for (AdapterList<?, ?, ?> childList : childrenListAdapter.values()) {
            if (childList != null && childList.isUpdated()) {
                return true;
            }
        }
        return false;
    }

    public boolean isReady() {
        return ready;
    }

    protected void prepareUpdate() {
        checkReady();
        if (builder == null) {
            builder = config.messageToBuilder().apply(message);
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


