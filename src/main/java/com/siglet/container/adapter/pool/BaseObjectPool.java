package com.siglet.container.adapter.pool;

import com.siglet.container.adapter.Adapter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public class BaseObjectPool<T extends Adapter> {

    private final Deque<T> pool;

    private final Supplier<T> objectCreator;

    public BaseObjectPool(int initialSize, Supplier<T> objectCreator) {
        this.objectCreator = objectCreator;
        this.pool = new ArrayDeque<>();
        for(int i = 0; i < initialSize; i++) {
            pool.add(objectCreator.get());
        }
    }

    protected synchronized T get() {
        T object = pool.poll();
        if (object == null) {
            object = objectCreator.get();
        }
        return object;
    }

    public synchronized void recycle(T object) {
        object.clear();
        pool.add(object);
    }

    public int size() {
        return pool.size();
    }


}
