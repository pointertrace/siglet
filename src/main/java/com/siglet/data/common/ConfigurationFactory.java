package com.siglet.data.common;

import java.util.Map;

public interface ConfigurationFactory<T> {

    T createConfig(Map<String,Object> localConfig, Map<String,Object> globalConfig);
}
