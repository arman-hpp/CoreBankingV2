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

public class CacheKeyGeneratorUtils implements KeyGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public @NotNull Object generate(@NotNull Object target, @NotNull Method method, Object @NotNull ... params) {
        var serviceName = target.getClass().getSimpleName();
        var methodName = method.getName();
        var paramString = Arrays.stream(params)
                .map(this::serializeParam)
                .collect(Collectors.joining("-"));
        return String.format("%s::%s::%s", serviceName, methodName, paramString);
    }

    private String serializeParam(Object param) {
        if (param == null) return "null";
        if (param instanceof String || param instanceof Number || param instanceof Boolean)
            return param.toString();
        if (param instanceof PaginationRequestDto paginationDto) {
            return String.format("page=%s,size=%s", paginationDto.getPageNumber(), paginationDto.getPageSize());
        } else if (param instanceof FilterInfoDto filterInfo) {
            return DigestUtils.md5Hex(convertToJson(filterInfo));
        } else {
            return convertToJson(param);
        }
    }

    private String convertToJson(Object param) {
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            return String.format("objHash=%s", param.hashCode());
        }
    }
}
