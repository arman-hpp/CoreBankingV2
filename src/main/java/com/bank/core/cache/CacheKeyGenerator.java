package com.bank.core.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * Generates cache keys for Spring Cache based on target class, method, and parameters.
 */
public final class CacheKeyGenerator implements KeyGenerator {
    private final CacheParameterSerializer parameterSerializer;

    public CacheKeyGenerator() {
        this.parameterSerializer = new CacheParameterSerializer(new ObjectMapper());
    }

    /**
     * Generates a cache key by combining target class name, method name, and serialized parameters.
     *
     * @param target the target object
     * @param method the method being invoked
     * @param params the method parameters
     * @return a unique cache key
     */
    @Override
    public @NotNull Object generate(@NotNull Object target, @NotNull Method method, Object @NotNull ... params) {
        var serviceName = target.getClass().getSimpleName();
        var methodName = method.getName();
        var paramString = parameterSerializer.serialize(params);
        return String.format("%s::%s::%s", serviceName, methodName, paramString);
    }
}