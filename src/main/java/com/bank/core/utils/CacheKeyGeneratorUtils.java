package com.bank.core.utils;

import com.bank.core.dtos.PaginationRequestDto;
import com.bank.core.dtos.filters.FilterInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Generates cache keys for Spring Cache based on target class, method, and parameters.
 */
public class CacheKeyGeneratorUtils implements KeyGenerator {
    private final CacheParameterSerializer CacheParameterSerializer =
            new CacheParameterSerializer(new ObjectMapper());

    /**
     * Generates a cache key using the target class name, method name, and serialized parameters.
     */
    @Override
    public @NotNull Object generate(@NotNull Object target, @NotNull Method method, Object @NotNull ... params) {
        var serviceName = target.getClass().getSimpleName();
        var methodName = method.getName();
        var paramString = CacheParameterSerializer.serialize(params);
        return String.format("%s::%s::%s", serviceName, methodName, paramString);
    }

    /**
     * Serializes method parameters into strings for cache key generation.
     */
    private record CacheParameterSerializer(ObjectMapper objectMapper) {
        public String serialize(Object @NotNull ... params) {
            return Arrays.stream(params)
                    .map(this::serializeObject)
                    .collect(Collectors.joining("-"));
        }

        private String serializeObject(Object param) {
            if (param == null) {
                return "null";
            }
            if (isSimpleType(param)) {
                return param.toString();
            }
            if (param instanceof PaginationRequestDto paginationDto) {
                return serializePaginationDto(paginationDto);
            } else if (param instanceof FilterInfoDto filterInfo) {
                return serializeFilterInfo(filterInfo);
            } else {
                return serializeToJson(param);
            }
        }

        private boolean isSimpleType(Object param) {
            return param instanceof String || param instanceof Number || param instanceof Boolean;
        }

        private String serializePaginationDto(PaginationRequestDto paginationDto) {
            return String.format("page=%d,size=%d", paginationDto.getPageNumber(), paginationDto.getPageSize());
        }

        private String serializeFilterInfo(FilterInfoDto filterInfo) {
            return DigestUtils.md5Hex(serializeToJson(filterInfo));
        }

        private String serializeToJson(Object param) {
            try {
                return objectMapper.writeValueAsString(param);
            } catch (JsonProcessingException e) {
                return String.format("objHash=%s", param.hashCode());
            }
        }
    }
}
