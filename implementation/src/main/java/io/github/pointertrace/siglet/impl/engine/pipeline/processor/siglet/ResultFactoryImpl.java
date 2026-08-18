package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet;

import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.SigletError;

public class ResultFactoryImpl implements ResultFactory {

    private static ResultFactory INSTANCE;

    public static void init() {
        INSTANCE = new ResultFactoryImpl();
    }

    public static  ResultFactory getInstance() {
        if (INSTANCE == null) {
            throw new SigletError("ResultFactory not initialized");
        }
        return INSTANCE;
    }

    private ResultFactoryImpl() {
    }

    @Override
    public Result drop() {
        return ResultImpl.drop();
    }

    @Override
    public Result proceed() {
        return ResultImpl.proceed();
    }

    @Override
    public Result proceed(String destination) {
        return ResultImpl.proceed(destination);
    }
}
