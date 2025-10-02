package io.github.pointertrace.siglet.impl.eventloop.processor.result;

import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;

public class ResultFactoryImpl implements ResultFactory {

    public static final ResultFactory INSTANCE = new ResultFactoryImpl();

    private ResultFactoryImpl() {}

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
